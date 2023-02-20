package PapachenBot.Models


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Serializable
data class SearchResult(
    @SerialName("search_objects") var products: ArrayList<Product> = arrayListOf()
){
    var olds : ArrayList<Product> = arrayListOf()

    fun getNews() : List<Product>{
        return products.filter { new -> olds.none { it.id == new.id } }
    }
}


@Serializable
data class Product(
    val id : String? = null,
    val title : String? = null,
    val description : String? = null,
    val distance : Float? = null,
    val price : Float? = null,
    val vendor : String? = null,
    val web_slug : String? = null,
    val modification_date : Long? = null,
    val flags : Flags? = Flags(),
    val location : Location? = Location(),
    val images : ArrayList<Image>? = arrayListOf()
)
{
    val url get() = "https://es.wallapop.com/item/$web_slug"

    fun getText() : String{
return """
★ ${title} ★
    💲 ${price}
    📍 ${location?.city}
    📆 ${if(modification_date != null) SimpleDateFormat("MM/dd/yyyy HH:mm:").format(Date(modification_date!!)) else null}
    🌐 ${url}
""".trimIndent()
}

}

@Serializable
data class Flags(
    var sold : Boolean? = null,
    var reserved : Boolean? = null,

){

}
@Serializable
data class Location(
    var city : String? = null
){

}

@Serializable
data class Image(
    var original : String? = null
){

}