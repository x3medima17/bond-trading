import jp.co.soramitsu.iroha.java.Query

fun main(){

    val pendingTxs = irohaAPI
        .query(
            Query.builder(nbcId, 1)
                .pendingTransactions
                .buildSigned(nbcKeypair)
        )
        .transactionsResponse
        .transactionsList


    println(
        pendingTxs
//            .filter { it.payload.reducedPayload.creatorAccountId == sberId }
    )

}