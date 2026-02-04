package com.escalatorstarter.pages.domain

import com.raquo.laminar.api.L._
import escalator.frontend.components.charts._
import escalator.frontend.components.charts.ChartTypes._
import java.time.Instant
import scala.util.Random

/**
  * Test page for demonstrating chart components with sample data
  */
object ChartsTestPage {

  def apply(): HtmlElement = {

    // Sample data generators
    val sampleCandles = generateSampleCandleData(100, 50000.0)
    val sampleLineData = generateSampleLineData(100, 100000.0)
    val sampleMultilineData = List(
      SeriesConfig("Portfolio A", generateSampleLineData(100, 100000.0), "#2196F3"),
      SeriesConfig("Portfolio B", generateSampleLineData(100, 95000.0), "#4CAF50"),
      SeriesConfig("Benchmark", generateSampleLineData(100, 98000.0), "#FF9800")
    )

    val candlesVar = Var(sampleCandles)
    val lineDataVar = Var(sampleLineData)
    val multilineDataVar = Var(sampleMultilineData)

    div(
      className := "min-h-screen bg-gray-50 dark:bg-gray-900 p-8",

      // Header
      div(
        className := "mb-8",
        h1(
          className := "text-3xl font-bold text-gray-900 dark:text-white mb-2",
          "Lightweight Charts Test Page"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400",
          "Interactive demonstration of chart components"
        )
      ),

      // Candlestick Chart Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Candlestick Chart"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "OHLC candlestick chart"
        ),
        CandlestickChart(
          data = candlesVar.signal,
          options = RocketChartOptions(height = 400),
          containerId = "test-candlestick-chart"
        ),
        div(
          className := "mt-4",
          button(
            className := "px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 mr-2",
            "Generate Random Data",
            onClick --> Observer[org.scalajs.dom.MouseEvent] { _ =>
              candlesVar.set(generateSampleCandleData(100, 50000.0 + Random.nextDouble() * 10000))
            }
          )
        )
      ),

      // Line Chart Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Line Chart"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Simple line chart for time series data"
        ),
        LineChart(
          data = lineDataVar.signal,
          color = "#00C853",
          lineWidth = 3,
          options = RocketChartOptions(height = 300),
          containerId = "test-line-chart"
        ),
        div(
          className := "mt-4",
          button(
            className := "px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 mr-2",
            "Refresh Data",
            onClick --> Observer[org.scalajs.dom.MouseEvent] { _ =>
              lineDataVar.set(generateSampleLineData(100, 100000.0 + Random.nextDouble() * 20000))
            }
          )
        )
      ),

      // Baseline Chart Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Baseline Chart"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Baseline chart with colored above/below base value (useful for P&L)"
        ),
        BaselineChart.simple(
          data = lineDataVar.signal,
          baseValue = 100000.0,
          options = RocketChartOptions(height = 300),
          containerId = "test-baseline-chart"
        )
      ),

      // Multi-line Chart Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Multi-line Comparison Chart"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Multiple series for portfolio comparisons (percentage mode)"
        ),
        MultilineChart.percentage(
          data = multilineDataVar.signal,
          options = RocketChartOptions(height = 400),
          containerId = "test-multiline-chart"
        ),
        div(
          className := "mt-4",
          button(
            className := "px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700 mr-2",
            "Refresh Data",
            onClick --> Observer[org.scalajs.dom.MouseEvent] { _ =>
              multilineDataVar.set(List(
                SeriesConfig("Portfolio A", generateSampleLineData(100, 100000.0 + Random.nextDouble() * 20000), "#2196F3"),
                SeriesConfig("Portfolio B", generateSampleLineData(100, 95000.0 + Random.nextDouble() * 15000), "#4CAF50"),
                SeriesConfig("Benchmark", generateSampleLineData(100, 98000.0 + Random.nextDouble() * 10000), "#FF9800")
              ))
            }
          )
        )
      ),

      // Area Chart Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Area Chart"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Filled line chart (area chart)"
        ),
        LineChart.area(
          data = lineDataVar.signal,
          lineColor = "#2196F3",
          topColor = "rgba(33, 150, 243, 0.56)",
          bottomColor = "rgba(33, 150, 243, 0.04)",
          options = RocketChartOptions(height = 300),
          containerId = "test-area-chart"
        )
      )
    )
  }

  // Helper: Generate sample candlestick data
  private def generateSampleCandleData(count: Int, basePrice: Double): List[CandleData] = {
    val now = Instant.now().getEpochSecond
    val random = new Random()

    (0 until count).map { i =>
      val time = now - ((count - i) * 3600)
      val open = basePrice + (random.nextDouble() - 0.5) * 1000
      val close = open + (random.nextDouble() - 0.5) * 500
      val high = scala.math.max(open, close) + random.nextDouble() * 200
      val low = scala.math.min(open, close) - random.nextDouble() * 200

      CandleData(
        time = time,
        open = open,
        high = high,
        low = low,
        close = close
      )
    }.toList
  }

  // Helper: Generate sample line data
  private def generateSampleLineData(count: Int, baseValue: Double): List[LineDataPoint] = {
    val now = Instant.now().getEpochSecond
    val random = new Random()
    var currentValue = baseValue

    (0 until count).map { i =>
      val time = now - ((count - i) * 3600)
      currentValue = currentValue + (random.nextDouble() - 0.48) * (baseValue * 0.02)
      LineDataPoint(time, currentValue)
    }.toList
  }
}
