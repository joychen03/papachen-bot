package PapachenBot.Controllers

import PapachenBot.Enums.Filters
import PapachenBot.Enums.Status

class StatusController {
    var select = false;
    var add = false
    var delete = false
    var deleteAll = false
    var setFilter = false
    var filter : Filters? = null

    fun changeStatus(status: Status){
        clear()
        when(status){
            Status.SELECT -> select = true
            Status.ADD -> add = true
            Status.DELETE -> delete = true
            Status.DELETE_ALL -> deleteAll = true
            Status.SET_FILTER -> setFilter = true
            else -> {}
        }
    }

    fun changeSelectedFilter(filters: Filters){
        filter = filters
    }

    fun clear() {
        select = false
        add = false
        delete = false
        deleteAll = false
        setFilter = false
        filter = null
    }
}

