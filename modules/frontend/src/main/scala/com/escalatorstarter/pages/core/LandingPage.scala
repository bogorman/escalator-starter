package com.escalatorstarter.pages.core

import com.raquo.laminar.api.L._
import escalator.frontend.components.ui.{Button, Card}
import escalator.frontend.components.layout.Container
import com.escalatorstarter.routes.Router

object LandingPage {

  def apply(): HtmlElement = {
    Container.fullHeight(
      div(
        className := "flex flex-col items-center justify-center min-h-screen",
        div(
          className := "text-center max-w-4xl mx-auto px-4",

          // Logo/Brand
          div(
            className := "mb-8",
            h1(
              className := "text-6xl font-bold text-gray-900 dark:text-white mb-4",
              "Escalator Starter"
            ),
            p(
              className := "text-xl text-gray-600 dark:text-gray-400",
              "Modern Full-Stack Scala Template"
            )
          ),

          // Description
          div(
            className := "mb-12",
            p(
              className := "text-lg text-gray-700 dark:text-gray-300 max-w-2xl mx-auto",
              "A modern full-stack Scala application template built with Laminar, Tailwind CSS, and the eSCALAtor framework. " +
              "Type-safe frontend and backend with shared models, beautiful UI components, and production-ready configuration."
            )
          ),

          // CTA Buttons
          div(
            className := "flex flex-col sm:flex-row gap-4 justify-center items-center",
            Button(
              text = "Sign Up",
              variant = Button.Variant.Primary,
              size = Button.Size.Large,
              onClickHandler = () => Router.navigateTo(Router.Page.Register)
            ),
            Button(
              text = "Log In",
              variant = Button.Variant.Outline,
              size = Button.Size.Large,
              onClickHandler = () => Router.navigateTo(Router.Page.Login)
            )
          ),

          // Features
          div(
            className := "mt-20 grid grid-cols-1 md:grid-cols-3 gap-8",

            featureCard(
              "⚡",
              "Fast Development",
              "Type-safe frontend and backend with shared models for rapid development."
            ),

            featureCard(
              "🎨",
              "Beautiful UI",
              "Pre-built components with Tailwind CSS styling and dark mode support."
            ),

            featureCard(
              "📦",
              "Ready to Deploy",
              "Production-ready build configuration with optimized bundle sizes included."
            )
          )
        )
      )
    )
  }

  private def featureCard(icon: String, title: String, description: String): HtmlElement = {
    Card(
      div(
        className := "text-center p-2",
        div(
          className := "text-4xl mb-4",
          icon
        ),
        h3(
          className := "text-xl font-semibold text-gray-900 dark:text-white mb-2",
          title
        ),
        p(
          className := "text-gray-600 dark:text-gray-400",
          description
        )
      )
    )
  }
}
