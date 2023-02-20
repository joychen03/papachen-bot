package PapachenBot.Models



import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


@Serializable
class Search(val name : String) {
    val keyWord get() = name.replace(" ", "+")
    val id = UUID.randomUUID().toString()
    var minPrice : Int? = null
    var maxPrice : Int? = null
    var condition : String? = null
    var orderBy : String? = null
    var distance : Int? = null
    var time_filter : String? = null
    //    var longitude : String? = null
    //    var latitude : String? = null

    var result : SearchResult = SearchResult()

    fun updateResult() : List<Product>{
        val url = getURL();
        val json = URL(url).readText()
        val newResult = Json{ignoreUnknownKeys = true}.decodeFromString<SearchResult>(json)
        result.olds = result.products
        result.products = newResult.products

        return result.getNews()
    }



    fun getURL() : String{
        val SEARCH_URL_BASE = "https://api.wallapop.com/api/v3/general/search/"

        var result = SEARCH_URL_BASE
        result += "?keywords=${keyWord}"
        result += if(minPrice != null) "&min_sale_price=$minPrice" else ""
        result += if(maxPrice != null) "&max_sale_price=$maxPrice" else ""
        result += if(condition != null) "&condition=$condition" else ""
        result += if(condition != null) "&time_filter=$time_filter" else ""
        result += if(orderBy != null) "&order_by=$orderBy" else ""
        result += if(distance != null) "&distance=$distance" else ""

        return result;
    }

    fun getText() : String{
        val formatDistance = if(distance != null) "${distance!! / 1000}Km" else ""

        var text = "Name: ${name}"
        text += if(minPrice != null) "\nMin Price: ${minPrice}" else ""
        text += if(maxPrice != null) "\nMax Price: ${maxPrice}" else ""
        text += if(condition != null) "\nCondition: ${condition}" else ""
        text += if(distance != null) "\nDistance: ${formatDistance}" else ""
        text += if(time_filter != null) "\nTime Filter: ${time_filter}" else ""
        text += if(orderBy != null) "\nOrderBy: ${orderBy}" else ""

        return text.trimIndent()
    }




}