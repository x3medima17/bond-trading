import iroha.protocol.Endpoint
import iroha.protocol.TransactionOuterClass
import jp.co.soramitsu.iroha.java.IrohaAPI
import jp.co.soramitsu.iroha.java.Query
import jp.co.soramitsu.iroha.java.Transaction
import jp.co.soramitsu.iroha.java.Utils


val nbcKeypair = Utils.parseHexKeypair(
    "313a07e6384776ed95447710d15e59148473ccfc052a681317a72a69f2a49910",
    "f101537e319568c765b2cc89698325604991dca57b9716b58016b253506cab70"
)


val sberKeypair = Utils.parseHexKeypair(
    "a467c1214e6368962e2d5eb81600280dd2d200608357483ef0927673007654d9",
    "dc48b8c143e0439922a525cf4e18725de66b4acd2a264e2c0be57f1e1d37368f"
)


val nbcId = "nbc@market"
val sberId = "sber@market"


val assetId = "usd#market"
val ncdId = "ncd001#market"

val irohaAPI = IrohaAPI("localhost", 50051)

fun getStatus(tx: TransactionOuterClass.Transaction): Endpoint.ToriiResponse = irohaAPI.txStatusSync(Utils.hash(tx))

fun sendAndWait(tx: TransactionOuterClass.Transaction): Endpoint.ToriiResponse {
    irohaAPI.transactionSync(tx)
    while (getStatus(tx).txStatus != Endpoint.TxStatus.COMMITTED
        && getStatus(tx).txStatus != Endpoint.TxStatus.REJECTED
    ) {
        println(getStatus(tx))
    }
    return getStatus(tx)
}

fun main() {
    val tx = Transaction.builder(nbcId)
        .createAsset("ncd001", "market", 2)
        .addAssetQuantity(ncdId, "1000")
        .sign(nbcKeypair)
        .build()

//    println(sendAndWait(tx))

    val lst = listOf<jp.co.soramitsu.iroha.java.Transaction>(
        Transaction.builder(nbcId)
            .transferAsset(nbcId, sberId, ncdId, "", "1000")
            .build(),

        Transaction.builder(sberId)
            .transferAsset(sberId, nbcId, assetId, "", "1000")
            .build()
    )



    val batch = Utils.createTxUnsignedAtomicBatch(lst)
    batch.toMutableList()[0].sign(nbcKeypair)
    val builtBatch = batch.map { it.build() }
    irohaAPI.transactionListSync(builtBatch)

    Thread.sleep(5_000)
//    println(batch)

    builtBatch.forEach {
        println(getStatus(it))
    }


    val pendingTxs = irohaAPI
        .query(
            Query.builder(nbcId, 1)
                .pendingTransactions
                .buildSigned(nbcKeypair)
        )
        .transactionsResponse
        .transactionsList


    println(pendingTxs)

}
