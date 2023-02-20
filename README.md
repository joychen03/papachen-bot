
# PAPACHEN BOT

This is a school project. It's a telegram bot written with Kotlin, you can use this bot to keep tracking a specific search on Wallapop (setting a good filter help to get valuable results), when there is new result on that search, you can get those new products by sending commands in the chat.

In the other hand, every search tracked is also stored as a JSON file on you local machine. The path of the JSON is on:

```bash
  Linux = /home/user/searches.json
  Windows = C:\Users\user\searches.json
```




## Tech Stack

**Language:** Kotlin

**Tags:** Telegram, Wallapop, JSON.


## License

[MIT](https://choosealicense.com/licenses/mit/)


## Run Locally

Clone the project

```bash
  git clone https://github.com/joychen03/papachen-bot.git
```

Define yor bot token on Main.kt file

```bash
  TOKEN = "YOUR BOT TOKEN HERE"
```


## Get Started

Once the program is running on your local machine, you can go to your telegram and the following command to start using it.

```bash
  /start
```

You will get a menu will all the options that you can do, follow the instruction. 

To add your first search:

```bash
  /add
```

To select other created searches:

```bash
  /select
```

To set filters on your search:

```bash
  /setfilter
```

To get all your current selected search result:

```bash
  /getall
```

To get only new search results (if the search is new, it has same effect as /getall):

```bash
  /update
```

To delete current selected search:

```bash
  /delete
```

More options see inside the menu of the bot.
