package com.escalatorstarter.pages.core

import com.raquo.laminar.api.L._
import escalator.frontend.components.ui.{Card, StatCard, Badge}
import com.escalatorstarter.state.AppState

/**
  * Main dashboard page shown after login
  */
object DashboardPage {

  def apply(): HtmlElement = {
    div(
      className := "space-y-6",

      // Welcome header
      div(
        className := "mb-8",
        h1(
          className := "text-3xl font-bold text-gray-900 dark:text-white mb-2",
          child <-- AppState.currentUser.signal.map {
            case Some(user) => s"Welcome back, ${user.fullName.getOrElse(user.email.email)}!"
            case None => "Welcome to your Dashboard"
          }
        ),
        p(
          className := "text-gray-600 dark:text-gray-400",
          "Here's what's happening with your application"
        )
      ),

      // Stats grid
      div(
        className := "grid grid-cols-1 md:grid-cols-3 gap-6 mb-8",

        StatCard(
          title = "Total Users",
          value = "1,234",
          change = Some("+12%"),
          trend = StatCard.Trend.Up,
          subtitle = Some("vs. last month")
        ),

        StatCard(
          title = "Active Sessions",
          value = "42",
          change = Some("+5%"),
          trend = StatCard.Trend.Up,
          subtitle = Some("currently online")
        ),

        StatCard(
          title = "Response Time",
          value = "125ms",
          change = Some("-8%"),
          trend = StatCard.Trend.Down,
          subtitle = Some("avg. response time")
        )
      ),

      // Content cards
      div(
        className := "grid grid-cols-1 md:grid-cols-2 gap-6",

        // Recent Activity card
        Card.withHeader(
          title = "Recent Activity",
          subtitle = Some("Latest updates from your application"),
          activityList()
        ),

        // Quick Actions card
        Card.withHeader(
          title = "Quick Actions",
          subtitle = Some("Common tasks and shortcuts"),
          quickActionsList()
        )
      ),

      // Getting Started card
      div(
        className := "mt-6",
        Card.withHeader(
          title = "Getting Started",
          subtitle = Some("Learn how to customize your Escalator Starter app"),
          gettingStartedContent()
        )
      )
    )
  }

  private def activityList(): HtmlElement = {
    div(
      className := "space-y-4",
      activityItem("User registration", "john@example.com registered", "2 minutes ago", Badge.Variant.Success),
      activityItem("Login attempt", "jane@example.com logged in", "15 minutes ago", Badge.Variant.Info),
      activityItem("Data update", "Settings were modified", "1 hour ago", Badge.Variant.Warning),
      activityItem("System event", "Database backup completed", "3 hours ago", Badge.Variant.Secondary)
    )
  }

  private def activityItem(title: String, description: String, time: String, badgeVariant: Badge.Variant): HtmlElement = {
    div(
      className := "flex items-start justify-between",
      div(
        className := "flex-1",
        div(
          className := "flex items-center gap-2 mb-1",
          span(className := "font-medium text-gray-900 dark:text-white", title),
          Badge(text = time, variant = badgeVariant, size = Badge.Size.Small)
        ),
        p(className := "text-sm text-gray-600 dark:text-gray-400", description)
      )
    )
  }

  private def quickActionsList(): HtmlElement = {
    div(
      className := "space-y-3",
      quickAction("📊", "View Analytics", "Check your application metrics"),
      quickAction("👥", "Manage Users", "Add or remove user accounts"),
      quickAction("⚙️", "Settings", "Configure application preferences"),
      quickAction("📝", "Documentation", "Read the developer guide")
    )
  }

  private def quickAction(icon: String, title: String, description: String): HtmlElement = {
    button(
      typ := "button",
      className := "w-full text-left p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors",
      div(
        className := "flex items-center gap-3",
        span(className := "text-2xl", icon),
        div(
          span(className := "block font-medium text-gray-900 dark:text-white", title),
          span(className := "block text-sm text-gray-600 dark:text-gray-400", description)
        )
      )
    )
  }

  private def gettingStartedContent(): HtmlElement = {
    div(
      className := "space-y-4",
      div(
        className := "prose dark:prose-invert max-w-none",
        p(
          "Welcome to Escalator Starter! This template provides a solid foundation for building modern web applications with Scala.js and Laminar."
        ),
        ul(
          className := "list-disc list-inside space-y-2",
          li("Customize the UI components in ", code("escalator-frontend/components")),
          li("Add new pages in ", code("components/pages")),
          li("Define API endpoints in ", code("api/ApiClient.scala")),
          li("Manage state in ", code("state/AppState.scala")),
          li("Configure routing in ", code("routes/Router.scala"))
        )
      )
    )
  }
}
