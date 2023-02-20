package PapachenBot


import PapachenBot.Controllers.SearchesController
import PapachenBot.Controllers.StatusController
import PapachenBot.Enums.*
import PapachenBot.Models.Search
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.*
import com.github.kotlintelegrambot.entities.*
import com.github.kotlintelegrambot.entities.ParseMode.MARKDOWN
import com.github.kotlintelegrambot.entities.ParseMode.MARKDOWN_V2
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton

//YOUR BOT TOKEN
const val TOKEN = "YOUR_BOT_TOKEN"

fun main() {

    val controller = SearchesController()
    val status = StatusController()
    val bot = bot {

        token = TOKEN

        dispatch {

            //INIT
            command("start"){
                bot.setMyCommands(listOf(
                    BotCommand("getall", "Get all searches"),
                    BotCommand("select", "Select search"),
                    BotCommand("add", "Add new search"),
                    BotCommand("update", "Check for new products for selected search"),
                    BotCommand("delete", "Delete selected search"),
                    BotCommand("setfilter", "Set filter for selected search"),
                    BotCommand("getresult", "Get results of the search"),
                    BotCommand("updateall", "Check for new products for selected search"),
                    BotCommand("deleteall", "Delete all searches"),
                    BotCommand("cancel", "Cancel process"),
                    BotCommand("menu", "Show menu option"),
                ))
            }

            text {

                if(message.entities?.get(0)?.type == MessageEntity.Type.BOT_COMMAND) return@text

                if(status.add){
                    controller.add(Search(text));

                    if(controller.isSelected()){
                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = "Search created and selected",
                            replyMarkup = getUserButtonsAfterSelect()
                        )
                    }

                    status.clear()
                    return@text

                }else if(status.deleteAll){
                    val responseText = if(text == "YES"){
                        controller.deleteAll()
                        "All searches deleted successfully"
                    }else{
                        "Delete all - canceled"
                    }

                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = responseText,
                        replyMarkup = getUserButtonsBeforeSelect()
                    )
                }

                if(controller.isSelected()){
                    if(status.delete){
                        var responseText = ""
                        if(text == "YES"){
                            responseText = if(controller.deleteSelected()){
                                "Search deleted"
                            }else{
                                "Something went wrong"
                            }
                            bot.sendMessage(
                                chatId = ChatId.fromId(message.chat.id),
                                text = responseText,
                                replyMarkup = getUserButtonsBeforeSelect()
                            )
                        }else{
                            responseText = "Delete canceled"
                            bot.sendMessage(
                                chatId = ChatId.fromId(message.chat.id),
                                text = responseText,
                                replyMarkup = getUserButtonsAfterSelect()
                            )
                        }

                    }else if(status.filter != null){
                            controller.setFilter(status.filter!!, text)

                            bot.sendMessage(
                                chatId = ChatId.fromId(message.chat.id),
                                text = controller.getSelected()?.getText() ?: "ERROR",
                                replyMarkup = getUserButtonsAfterSelect()
                            )

                            status.clear()
                    }

                }else{
                    if(status.select){
                        controller.select(text.toInt()-1)
                        val selected = controller.getSelected()

                        if(selected == null){
                            if(controller.getSearches().isEmpty()){
                                bot.sendMessage(
                                    chatId = ChatId.fromId(message.chat.id),
                                    text = """
                                    No search available, create one using:
                                    */add*
                                """.trimIndent(),
                                    parseMode = MARKDOWN_V2
                                )
                            }else{
                                bot.sendMessage(
                                    chatId = ChatId.fromId(message.chat.id),
                                    text = "Please select the correct search"
                                )
                            }
                        }else{
                            bot.sendMessage(
                                chatId = ChatId.fromId(message.chat.id),
text = """
SELECTED  📌

${selected.getText()}
""".trimIndent(),
                                replyMarkup = getUserButtonsAfterSelect()
                            )
                            status.clear()
                        }

                    }else{

                    }
                }
            }

            command("select"){
                val searches = controller.getSearches()

                if(searches.isEmpty()){
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = """
                            No search available, create one using:
                            */add*
                        """.trimIndent(),
                        parseMode = MARKDOWN_V2,
                        replyMarkup = getUserButtonsBeforeSelect()
                    )
                    return@command
                }
                status.changeStatus(Status.SELECT)

                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = controller.getSearchesText(),
                    replyMarkup = KeyboardReplyMarkup(keyboard = getNumbersKB(controller.searches.size), resizeKeyboard = true, oneTimeKeyboard = true),
                    parseMode = MARKDOWN_V2
                )
            }

            command("cancel"){
                status.clear()
                if(controller.isSelected()){
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Canceled",
                        replyMarkup = getUserButtonsAfterSelect()
                    )
                }else{
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Canceled",
                        replyMarkup = getUserButtonsBeforeSelect()
                    )
                }

            }

            command("menu"){
                status.clear()

                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = "Back to menu",
                    replyMarkup = getUserButtonsBeforeSelect()
                )
            }

            command("getall"){
                status.clear()

                val searches = controller.getSearches()
                var count = 1

                if(searches.isNotEmpty()){
                    searches.forEach {
val text = """
🚩 Search id : $count
☟
${it.getText()}
☟
🔍 Result: ${it.result.products.size} results
""".trimIndent()

                        bot.sendMessage(
                            chatId = ChatId.fromId(message.chat.id),
                            text = text,
                        )

                        count++
                    }

                }else{
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = """
                            No search available, create one using:
                            */add*
                        """.trimIndent(),
                        parseMode = MARKDOWN_V2,
                        replyMarkup = getUserButtonsBeforeSelect()
                    )
                }

            }

            command("add"){
                status.changeStatus(Status.ADD)

                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = """
                        What do you want to search? Write the keywords.
                        ‼️*USE ONLY SPACES TO SEPARATE WORDS*‼️
                    """.trimIndent(),
                    parseMode = MARKDOWN_V2
                )
            }


            command("update"){
                status.clear()
                if(!controller.isSelected()){
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "PLEASE SELECT A SEARCH",
                        replyMarkup = getUserButtonsBeforeSelect(),
                    )
                    return@command
                }

                val newResults = controller.getUpdates()

                if(newResults.isEmpty()){
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = """
                            🔎 No new result found
                            ❗ Some filter may reduce your search result, check it with */getresult* to see current serach result
                        """.trimIndent(),
                        parseMode = MARKDOWN_V2
                    )
                    return@command
                }
                newResults.forEach {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = it.getText()
                    )
                }

            }


            command("delete"){
                status.clear()
                if(!controller.isSelected()){
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "PLEASE SELECT A SEARCH",
                        replyMarkup = getUserButtonsBeforeSelect(),
                    )
                    return@command
                }

                status.changeStatus(Status.DELETE)
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
text = """
Are you sure to delete this search?
${controller.getSelected()?.getText()}
""".trimIndent(),
                    replyMarkup = getBooelanOptions()
                )
            }

            command("updateall"){
                status.clear()

                if(controller.getSearches().isEmpty()){
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = """
                                    No search available, create one using:
                                    */add*
                                """.trimIndent(),
                        parseMode = MARKDOWN_V2
                    )
                    return@command
                }

                val newResults = controller.getAllUpdates();

                if(newResults.isEmpty()){
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = """
                            🔎 No new result found
                            ❗ Some filter may reduce your search result, check it with */getresult* to see current serach result
                        """.trimIndent(),
                        parseMode = MARKDOWN_V2
                    )
                    return@command
                }

                newResults.forEach {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = it.getText()
                    )
                }
            }

            command("deleteall"){

                if(controller.getSearches().isEmpty()){
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = """
                                    No search available, create one using:
                                    */add*
                                """.trimIndent(),
                        parseMode = MARKDOWN_V2
                    )
                    return@command
                }

                status.changeStatus(Status.DELETE_ALL)

                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = "*WARNING* ARE YOU SURE TO DELETE ALL SEARCHES?",
                    parseMode = MARKDOWN_V2,
                    replyMarkup = getBooelanOptions()
                )
            }

            command("getresult"){
                status.clear()
                if(!controller.isSelected()){
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "PLEASE SELECT A SEARCH",
                        replyMarkup = getUserButtonsBeforeSelect(),
                    )
                    return@command
                }

                val searchResults = controller.getResults();

                if(searchResults.isEmpty()){
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "🔎 No new result found"
                    )
                    return@command
                }

                searchResults.forEach {
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = it.getText()
                    )
                }

            }

            command("setfilter"){
                if(!controller.isSelected()){
                    bot.sendMessage(
                        chatId = ChatId.fromId(message.chat.id),
                        text = "PLEASE SELECT A SEARCH",
                        replyMarkup = getUserButtonsBeforeSelect(),
                    )
                    return@command
                }

//                status.changeStatus(Status.SET_FILTER)

                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    text = "*CHOOSE FILTER TYPE*",
                    replyMarkup = getFiltersInlineKB(),
                    parseMode = MARKDOWN_V2
                )
            }

            callbackQuery("minPrice") {
                val chatId = callbackQuery.message?.chat?.id ?: return@callbackQuery

                if(!controller.isSelected()){
                    bot.sendMessage(
                        chatId = ChatId.fromId(chatId),
                        text = "PLEASE SELECT A SEARCH",
                        replyMarkup = getUserButtonsBeforeSelect(),
                    )
                    return@callbackQuery
                }

                status.changeSelectedFilter(Filters.MIN_PRICE)

                bot.sendMessage(
                    chatId = ChatId.fromId(chatId),
                    text = "Introduce the *MIN* price for this search",
                    parseMode = MARKDOWN
                )

            }

            callbackQuery("maxPrice") {
                val chatId = callbackQuery.message?.chat?.id ?: return@callbackQuery

                if(!controller.isSelected()){
                    bot.sendMessage(
                        chatId = ChatId.fromId(chatId),
                        text = "PLEASE SELECT A SEARCH",
                        replyMarkup = getUserButtonsBeforeSelect(),
                        parseMode = MARKDOWN
                    )
                    return@callbackQuery
                }

                status.changeSelectedFilter(Filters.MAX_PRICE)

                bot.sendMessage(
                    chatId = ChatId.fromId(chatId),
                    text = "Introduce the MAX price for this search",
                    parseMode = MARKDOWN
                )

            }

            callbackQuery("distance") {
                val chatId = callbackQuery.message?.chat?.id ?: return@callbackQuery

                if(!controller.isSelected()){
                    bot.sendMessage(
                        chatId = ChatId.fromId(chatId),
                        text = "PLEASE SELECT A SEARCH",
                        replyMarkup = getUserButtonsBeforeSelect(),
                    )
                    return@callbackQuery
                }

                status.changeSelectedFilter(Filters.DISTANCE)

                bot.sendMessage(
                    chatId = ChatId.fromId(chatId),
                    text = "Introduce the distance filter in *Kilometers*",
                    parseMode = MARKDOWN
                )

            }


            callbackQuery("condition") {
                val chatId = callbackQuery.message?.chat?.id ?: return@callbackQuery

                if(!controller.isSelected()){
                    bot.sendMessage(
                        chatId = ChatId.fromId(chatId),
                        text = "PLEASE SELECT A SEARCH",
                        replyMarkup = getUserButtonsBeforeSelect(),
                    )
                    return@callbackQuery
                }

                status.changeSelectedFilter(Filters.CONDITION)

                bot.sendMessage(
                    chatId = ChatId.fromId(chatId),
                    text = "Choose *Condition* filter options",
                    replyMarkup = getConditionOptions(),
                    parseMode = MARKDOWN
                )

            }

            callbackQuery("timeFilter") {
                val chatId = callbackQuery.message?.chat?.id ?: return@callbackQuery

                if(!controller.isSelected()){
                    bot.sendMessage(
                        chatId = ChatId.fromId(chatId),
                        text = "PLEASE SELECT A SEARCH",
                        replyMarkup = getUserButtonsBeforeSelect(),
                    )
                    return@callbackQuery
                }

                status.changeSelectedFilter(Filters.TIME_FILTER)

                bot.sendMessage(
                    chatId = ChatId.fromId(chatId),
                    text = "Select the *Time filter* option",
                    replyMarkup = getTimeFilterOptions(),
                    parseMode = MARKDOWN
                )

            }

            callbackQuery("orderBy") {
                val chatId = callbackQuery.message?.chat?.id ?: return@callbackQuery

                if(!controller.isSelected()){
                    bot.sendMessage(
                        chatId = ChatId.fromId(chatId),
                        text = "PLEASE SELECT A SEARCH",
                        replyMarkup = getUserButtonsBeforeSelect(),
                    )
                    return@callbackQuery
                }

                status.changeSelectedFilter(Filters.ORDER_BY)

                bot.sendMessage(
                    chatId = ChatId.fromId(chatId),
                    text = "Select the *OrderBy* filter option",
                    replyMarkup = getOrderByOptions(),
                    parseMode = MARKDOWN_V2
                )

            }

        }
    }
    bot.startPolling()


}


fun getFiltersInlineKB(): InlineKeyboardMarkup {

    return InlineKeyboardMarkup.create(
        listOf(
            InlineKeyboardButton.CallbackData(Filters.ORDER_BY.name, Filters.ORDER_BY.id),
        ),
        listOf(
            InlineKeyboardButton.CallbackData(Filters.MIN_PRICE.name, Filters.MIN_PRICE.id),
            InlineKeyboardButton.CallbackData(Filters.MAX_PRICE.name, Filters.MAX_PRICE.id),
        ),
        listOf(
            InlineKeyboardButton.CallbackData(Filters.CONDITION.name, Filters.CONDITION.id),
        ),
        listOf(
            InlineKeyboardButton.CallbackData(Filters.DISTANCE.name, Filters.DISTANCE.id),
        ),
        listOf(
            InlineKeyboardButton.CallbackData(Filters.TIME_FILTER.name, Filters.TIME_FILTER.id),
        ),
    )
}

fun getUserButtonsBeforeSelect(isOneTimeSelect : Boolean = true) : KeyboardReplyMarkup{

    val buttons = listOf(
        listOf(
            KeyboardButton("/select"),
        ),
        listOf(
            KeyboardButton("/add"),
        ),
        listOf(
            KeyboardButton("/getall"),
            KeyboardButton("/updateall"),
            KeyboardButton("/deleteall"),
        ),
    )

    return KeyboardReplyMarkup(
        keyboard = buttons,
        resizeKeyboard = true,
        oneTimeKeyboard = isOneTimeSelect)
}

fun getUserButtonsAfterSelect(isOneTimeSelect : Boolean = true) : KeyboardReplyMarkup{

    val result = listOf(
        listOf(
            KeyboardButton("/getresult"),
            KeyboardButton("/update"),
            KeyboardButton("/delete"),
        ),
        listOf(
            KeyboardButton("/setfilter"),
        ),
        listOf(
            KeyboardButton("/cancel"),
            KeyboardButton("/menu"),
        ),
    )

    return KeyboardReplyMarkup(
        keyboard = result,
        resizeKeyboard = true,
        oneTimeKeyboard = isOneTimeSelect
    )
}


fun getNumbersKB(quantity : Int, rowLimit : Int = 5) : List<List<KeyboardButton>>{

    val result = mutableListOf<List<KeyboardButton>>()
    var row = mutableListOf<KeyboardButton>()
    var count = 0

    for (i in 1..quantity){
        if(count == rowLimit){
            count = 0
            result.add(row)
            row = mutableListOf()
        }
        row.add(
            KeyboardButton("${i}")
        )
        count ++
    }

    if(row.isNotEmpty()){
        result.add(row)
    }

    return result;

}


fun getTimeFilterOptions(isOneTimeSelect : Boolean = true): KeyboardReplyMarkup {
    var result = mutableListOf<List<KeyboardButton>>()

    TimeFilter.values().forEach {
        result.add(
            listOf(KeyboardButton(it.name))
        )

    }
    return KeyboardReplyMarkup(
        keyboard = result,
        resizeKeyboard = true,
        oneTimeKeyboard = isOneTimeSelect)
}

fun getOrderByOptions(isOneTimeSelect : Boolean = true): KeyboardReplyMarkup {
    var result = mutableListOf<List<KeyboardButton>>()
    OrderBy.values().forEach {
        result.add(
            listOf(KeyboardButton(it.name))
        )
    }

    return KeyboardReplyMarkup(
        keyboard = result,
        resizeKeyboard = true,
        oneTimeKeyboard = isOneTimeSelect
    )
}

fun getConditionOptions(isOneTimeSelect : Boolean = true): KeyboardReplyMarkup {
    var result = mutableListOf<List<KeyboardButton>>()
    Condition.values().forEach {
        result.add(
            listOf(KeyboardButton(it.name))
        )
    }

    return KeyboardReplyMarkup(
        keyboard = result,
        resizeKeyboard = true,
        oneTimeKeyboard = isOneTimeSelect
    )
}

fun getBooelanOptions(isOneTimeSelect : Boolean = true) : KeyboardReplyMarkup{
    val result = listOf(
        listOf(
            KeyboardButton("YES"),
        ),
        listOf(
            KeyboardButton("NO"),
        ),
    )

    return KeyboardReplyMarkup(
        keyboard = result,
        resizeKeyboard = true,
        oneTimeKeyboard = isOneTimeSelect
    )
}


