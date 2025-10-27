package com.escalatorstarter.components.pages

import com.raquo.laminar.api.L._
import escalator.frontend.components.tables._
import org.scalajs.dom
import scala.util.Random

/**
  * Test page for demonstrating DataTable component
  */
object DataTableTestPage {

  // Sample data models
  case class Trade(
    id: String,
    symbol: String,
    side: String,
    quantity: Double,
    price: Double,
    timestamp: Long
  )

  case class Backtest(
    id: String,
    versionId: String,
    status: String,
    cagr: Double,
    sharpe: Double,
    trades: Int,
    createdAt: Long
  )

  case class User(
    id: Long,
    name: String,
    email: String,
    role: String,
    active: Boolean
  )

  def apply(): HtmlElement = {

    // State for different examples
    val tradesVar = Var(generateSampleTrades(10))
    val tradesLoadingVar = Var(false)

    val backtestsVar = Var(generateSampleBacktests(8))
    val backtestsLoadingVar = Var(false)

    val usersVar = Var(List.empty[User])
    val usersLoadingVar = Var(false)

    div(
      className := "min-h-screen bg-gray-50 dark:bg-gray-900 p-8",

      // Header
      div(
        className := "mb-8",
        h1(
          className := "text-3xl font-bold text-gray-900 dark:text-white mb-2",
          "DataTable Component Test Page"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400",
          "Interactive demonstration of reusable DataTable component"
        )
      ),

      // Trades Table Example
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Trades Table"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "DataTable with trade data, row click handler, and loading states"
        ),
        DataTable(
          data = tradesVar.signal,
          columns = List(
            DataTable.textColumn[Trade](
              header = "Symbol",
              accessor = _.symbol,
              sortable = true
            ),
            Column[Trade](
              header = "Side",
              render = trade => {
                val (bgColor, textColor, text) = if (trade.side == "buy") {
                  ("bg-green-100", "text-green-800", "BUY")
                } else {
                  ("bg-red-100", "text-red-800", "SELL")
                }
                span(
                  className := s"px-2 py-1 text-xs font-medium rounded-full $bgColor $textColor",
                  text
                )
              },
              sortable = true,
              sortBy = Some(_.side)
            ),
            DataTable.numberColumn[Trade](
              header = "Quantity",
              accessor = _.quantity,
              format = qty => f"$qty%.4f",
              sortable = true
            ),
            DataTable.numberColumn[Trade](
              header = "Price",
              accessor = _.price,
              format = price => f"$$$price%.2f",
              sortable = true
            ),
            DataTable.numberColumn[Trade](
              header = "Total",
              accessor = t => t.quantity * t.price,
              format = total => f"$$$total%.2f",
              sortable = true
            ),
            Column[Trade](
              header = "Time",
              render = trade => span(formatTimestamp(trade.timestamp)),
              alignment = ColumnAlignment.Right,
              sortable = true,
              sortBy = Some(_.timestamp)
            )
          ),
          isLoading = tradesLoadingVar.signal,
          onRowClick = Some((trade: Trade) => {
            dom.console.log(s"Clicked trade: ${trade.id}")
            dom.window.alert(s"Clicked trade ${trade.symbol} - ${trade.side}")
          }),
          emptyMessage = "No trades found",
          options = TableOptions.default,
          initialSort = Some(SortState(5, SortDirection.Descending))  // Sort by time, newest first
        ),
        div(
          className := "mt-4 flex gap-2",
          button(
            className := "px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700",
            "Refresh Data",
            onClick --> Observer[dom.MouseEvent] { _ =>
              tradesLoadingVar.set(true)
              setTimeout(() => {
                tradesVar.set(generateSampleTrades(10))
                tradesLoadingVar.set(false)
              }, 1000)
            }
          ),
          button(
            className := "px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700",
            "Clear Data",
            onClick --> Observer[dom.MouseEvent] { _ =>
              tradesVar.set(List.empty)
            }
          )
        )
      ),

      // Backtests Table Example
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Backtests Table"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "DataTable with backtest results, custom rendering, and badges"
        ),
        DataTable(
          data = backtestsVar.signal,
          columns = List(
            DataTable.textColumn[Backtest](
              header = "Version ID",
              accessor = _.versionId,
              sortable = true
            ),
            Column[Backtest](
              header = "Status",
              render = bt => {
                val (bgColor, textColor) = bt.status match {
                  case "completed" => ("bg-green-100", "text-green-800")
                  case "running" => ("bg-blue-100", "text-blue-800")
                  case "failed" => ("bg-red-100", "text-red-800")
                  case _ => ("bg-gray-100", "text-gray-800")
                }
                span(
                  className := s"px-2 py-1 text-xs font-medium rounded-full $bgColor $textColor uppercase",
                  bt.status
                )
              },
              sortable = true,
              sortBy = Some(_.status)
            ),
            DataTable.numberColumn[Backtest](
              header = "CAGR",
              accessor = _.cagr,
              format = cagr => f"${cagr}%.2f%%",
              sortable = true
            ),
            DataTable.numberColumn[Backtest](
              header = "Sharpe",
              accessor = _.sharpe,
              format = sharpe => f"$sharpe%.2f",
              sortable = true
            ),
            DataTable.numberColumn[Backtest](
              header = "Trades",
              accessor = _.trades.toDouble,
              format = trades => trades.toInt.toString,
              sortable = true
            ),
            Column[Backtest](
              header = "Created",
              render = bt => span(formatTimestamp(bt.createdAt)),
              alignment = ColumnAlignment.Right,
              sortable = true,
              sortBy = Some(_.createdAt)
            )
          ),
          isLoading = backtestsLoadingVar.signal,
          onRowClick = Some((bt: Backtest) => {
            dom.console.log(s"Clicked backtest: ${bt.id}")
          }),
          options = TableOptions(
            striped = true,
            hoverable = true
          ),
          initialSort = Some(SortState(2, SortDirection.Descending))  // Sort by CAGR, best first
        ),
        div(
          className := "mt-4",
          button(
            className := "px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700",
            "Toggle Loading",
            onClick --> Observer[dom.MouseEvent] { _ =>
              backtestsLoadingVar.update(!_)
            }
          )
        )
      ),

      // Empty State Example
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Empty State Table"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "DataTable with empty data and custom empty state message"
        ),
        DataTable(
          data = usersVar.signal,
          columns = List(
            DataTable.textColumn[User](
              header = "Name",
              accessor = _.name
            ),
            DataTable.textColumn[User](
              header = "Email",
              accessor = _.email
            ),
            DataTable.badgeColumn[User](
              header = "Role",
              accessor = _.role,
              colorClass = user => user.role match {
                case "admin" => "bg-purple-100 text-purple-800"
                case "user" => "bg-blue-100 text-blue-800"
                case _ => "bg-gray-100 text-gray-800"
              }
            ),
            Column[User](
              header = "Status",
              render = user => {
                val (bgColor, textColor, text) = if (user.active) {
                  ("bg-green-100", "text-green-800", "Active")
                } else {
                  ("bg-gray-100", "text-gray-800", "Inactive")
                }
                span(
                  className := s"px-2 py-1 text-xs font-medium rounded-full $bgColor $textColor",
                  text
                )
              },
              alignment = ColumnAlignment.Center
            )
          ),
          isLoading = usersLoadingVar.signal,
          emptyMessage = "No users found. Add your first user to get started.",
          options = TableOptions(
            emptyStateIcon = Some("👥"),
            emptyStateAction = Some(("Add User", () => {
              val newUser = User(
                id = System.currentTimeMillis(),
                name = s"User ${Random.nextInt(100)}",
                email = s"user${Random.nextInt(100)}@example.com",
                role = if (Random.nextBoolean()) "admin" else "user",
                active = Random.nextBoolean()
              )
              usersVar.update(newUser :: _)
            }))
          )
        ),
        div(
          className := "mt-4",
          button(
            className := "px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700",
            "Load Sample Users",
            onClick --> Observer[dom.MouseEvent] { _ =>
              usersVar.set(generateSampleUsers(5))
            }
          )
        )
      ),

      // Compact Table Example
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Compact Table"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "DataTable with compact option and minimal styling"
        ),
        DataTable(
          data = Var(generateSampleTrades(5)).signal,
          columns = List(
            DataTable.textColumn[Trade](
              header = "Symbol",
              accessor = _.symbol
            ),
            DataTable.textColumn[Trade](
              header = "Side",
              accessor = _.side
            ),
            DataTable.numberColumn[Trade](
              header = "Qty",
              accessor = _.quantity,
              format = qty => f"$qty%.2f"
            ),
            DataTable.numberColumn[Trade](
              header = "Price",
              accessor = _.price,
              format = price => f"$$$price%.2f"
            )
          ),
          options = TableOptions.compact
        )
      )
    )
  }

  // Helper functions
  private def generateSampleTrades(count: Int): List[Trade] = {
    val symbols = List("BTCUSDT", "ETHUSDT", "ADAUSDT", "SOLUSDT", "DOTUSDT")
    val random = new Random()

    (1 to count).map { i =>
      val symbol = symbols(random.nextInt(symbols.length))
      Trade(
        id = s"trade-$i",
        symbol = symbol,
        side = if (random.nextBoolean()) "buy" else "sell",
        quantity = 0.1 + random.nextDouble() * 10.0,
        price = 1000.0 + random.nextDouble() * 50000.0,
        timestamp = System.currentTimeMillis() - (i * 3600000)
      )
    }.toList
  }

  private def generateSampleBacktests(count: Int): List[Backtest] = {
    val statuses = List("completed", "running", "failed")
    val random = new Random()

    (1 to count).map { i =>
      Backtest(
        id = s"backtest-$i",
        versionId = s"v1.${i}.0",
        status = statuses(random.nextInt(statuses.length)),
        cagr = -20.0 + random.nextDouble() * 100.0,
        sharpe = random.nextDouble() * 3.0,
        trades = 50 + random.nextInt(500),
        createdAt = System.currentTimeMillis() - (i * 86400000)
      )
    }.toList
  }

  private def generateSampleUsers(count: Int): List[User] = {
    val names = List("Alice Smith", "Bob Johnson", "Carol Williams", "David Brown", "Eve Davis")
    val roles = List("admin", "user")
    val random = new Random()

    (1 to count).map { i =>
      val name = names(i % names.length)
      User(
        id = i.toLong,
        name = name,
        email = s"${name.toLowerCase.replace(" ", ".")}@example.com",
        role = roles(random.nextInt(roles.length)),
        active = random.nextBoolean()
      )
    }.toList
  }

  private def formatTimestamp(timestamp: Long): String = {
    val date = new scalajs.js.Date(timestamp.toDouble)
    s"${date.toLocaleDateString()} ${date.toLocaleTimeString()}"
  }

  private def setTimeout(callback: () => Unit, delay: Int): Unit = {
    dom.window.setTimeout(() => callback(), delay)
  }
}
