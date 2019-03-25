import jp.co.soramitsu.iroha.java.Query
import jp.co.soramitsu.iroha.java.Transaction
import jp.co.soramitsu.iroha.java.Utils

fun main() {

    val pendingTxs = irohaAPI
        .query(
            Query.builder(sberId, 1)
                .pendingTransactions
                .buildSigned(sberKeypair)
        )
        .transactionsResponse
        .transactionsList


    val nbcTime = pendingTxs
        .map { it.payload.reducedPayload }
        .findLast { it.creatorAccountId == nbcId }!!
        .createdTime

    val sberTime = pendingTxs
        .map { it.payload.reducedPayload }
        .findLast { it.creatorAccountId == sberId }!!
        .createdTime

    val lst = listOf<jp.co.soramitsu.iroha.java.Transaction>(
        Transaction.builder(nbcId)
            .transferAsset(nbcId, sberId, ncdId, "", "1000")
            .setCreatedTime(nbcTime)
            .build(),

        Transaction.builder(sberId)
            .transferAsset(sberId, nbcId, assetId, "", "1000")
            .setCreatedTime(sberTime)
            .build()
    )



    val batch = Utils.createTxUnsignedAtomicBatch(lst)
    batch.toMutableList()[1].sign(sberKeypair)
    val builtBatch = batch.map { it.build() }
    irohaAPI.transactionListSync(builtBatch)


}