package PapachenBot.Enums

enum class Filters(val text : String, val id : String) {
    MIN_PRICE("From price", "minPrice"),
    MAX_PRICE("To price", "maxPrice"),
    CONDITION("Condition", "condition"),
    DISTANCE("Distance", "distance"),
    TIME_FILTER("Time filter", "timeFilter"),
    ORDER_BY("Order by", "orderBy"),
}
