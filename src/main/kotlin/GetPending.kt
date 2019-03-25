import jp.co.soramitsu.iroha.java.Query

fun main(){
    val pendingTxs = irohaAPI
        .query(
            Query.builder(sberId, 1)
                .pendingTransactions
                .buildSigned(sberKeypair)
        )
        .transactionsResponse
        .transactionsList

    println(pendingTxs)
}