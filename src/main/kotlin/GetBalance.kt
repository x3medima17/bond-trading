

import jp.co.soramitsu.iroha.java.Query

fun main() {

    // Check BobAlice an Tim balance
    val nbcBalance = irohaAPI
        .query(
            Query.builder(nbcId, 1)
                .getAccountAssets(nbcId)
                .buildSigned(nbcKeypair)
        )
    val sberBalance = irohaAPI
        .query(
            Query.builder(nbcId, 1)
                .getAccountAssets(sberId)
                .buildSigned(nbcKeypair)
        )

    println(nbcBalance)
    println(sberBalance)
}