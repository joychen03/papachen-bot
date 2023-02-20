package PapachenBot.Enums

enum class OrderBy(val value : String) {
    NEWEST("newest"),
    RELEVANCE("most_relevance"),
    CLOSEST("closest"),
    PRICE_LOW("price_low_to_high"),
    PRICE_HIGH("price_high_to_low")
}