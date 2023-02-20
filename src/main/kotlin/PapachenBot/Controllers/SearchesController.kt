package PapachenBot.Controllers


import PapachenBot.Enums.Condition
import PapachenBot.Enums.Filters
import PapachenBot.Enums.OrderBy
import PapachenBot.Enums.TimeFilter
import PapachenBot.Models.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SearchesController() {

    private val LocalJsonFile = File(System.getProperty("user.home")).resolve("searches.json")

    var searches : ArrayList<Search> = arrayListOf()
    var selected : Int? = null

    init {
        getLocalWatchList();
    }

    private fun getLocalWatchList(){
        try {
            if(LocalJsonFile.exists() && LocalJsonFile.readText() != ""){
                val json = LocalJsonFile.readText()
                println("GO FILE: ${LocalJsonFile.absolutePath}")
                this.searches = Json.decodeFromString(json)
            }
        }catch (e : Exception){
            println(e)
        }
    }

    fun getSelected(): Search? {
        return if(selected == null) null else searches[selected!!]
    }

    fun getSearches() : List<Search>{
        return searches
    }

    fun getSearchesText(): String {
        var result : String = ""
        for(i in 1..searches.size){
            result += "*$i* 👉 ${searches[i-1].name}\n"
        }
        return result
    }


    fun getResults() : List<Product>  {
        return if (isSelected()){
            searches[selected!!].result.products
        }else{
            arrayListOf()
        }
    }


    fun getUpdates() : List<Product>{
        return if (isSelected()){
            val result = mutableListOf<Product>()
            result.addAll(searches[selected!!].updateResult())
            saveWatchList()
            result
        }else{
            arrayListOf()
        }
    }

    fun getAllUpdates() : List<Product>{
        val result = mutableListOf<Product>()
        searches.forEach {
            result.addAll(it.updateResult())
        }
        saveWatchList()
        return result
    }

    fun select(index : Int) {
        if(index < 0 || index > searches.lastIndex){
            selected = null
        }else{
            selected = index
        }
    }

    fun add(search: Search){
        //Fetch result data into search
        search.updateResult()

        //Add search into watchlist
        searches.add(search)

        //Select current added search
        select(searches.lastIndex);

        //Save
        saveWatchList()
    }

    fun setFilter(filter: Filters, value : String) : Boolean {

        if(isSelected()){
            val search = searches[selected!!]
            when(filter){
                Filters.CONDITION-> search.condition = Condition.valueOf(value).value
                Filters.MIN_PRICE-> search.minPrice = value.toInt()
                Filters.MAX_PRICE-> search.maxPrice = value.toInt()
                Filters.TIME_FILTER-> search.time_filter = TimeFilter.valueOf(value).value
                Filters.DISTANCE-> search.distance = value.toInt() * 1000
                Filters.ORDER_BY-> search.orderBy = OrderBy.valueOf(value).value
            }

            saveWatchList()
            return true
        }else{
            return false
        }
    }

    fun deleteSelected(): Boolean {
        return if(isSelected()) {
            searches.removeAt(selected!!)
            selected = null;
            saveWatchList()
            true
        }else{
            false
        }
    }

    fun deleteAll() {
        searches = arrayListOf()
        selected = null;
        saveWatchList()
    }




    fun isSelected() : Boolean{
        return selected != null
    }

    private fun getIndex(id: String): Int {
        return searches.indexOfFirst { it.id == id }
    }

    private fun saveWatchList(){
        val json = Json.encodeToString(searches)
        LocalJsonFile.writeText(json)
    }




}