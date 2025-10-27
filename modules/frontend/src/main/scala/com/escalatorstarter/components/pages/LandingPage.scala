package com.escalatorstarter.components.pages

import com.raquo.laminar.api.L._
import escalator.frontend.components.ui.{Button, Card}
import escalator.frontend.components.layout.Container
import com.escalatorstarter.routes.Router

/**
  * Landing page - first page users see when they visit the app
  */
object LandingPage {

  def apply(): HtmlElement = {
    Container.fullHeight(
      // Centered content
      div(
        className := "flex flex-col items-center justify-center min-h-screen",

        // Hero section
        div(
          className := "text-center mb-12",
          h1(
            className := "text-5xl font-bold text-gray-900 dark:text-white mb-4",
            "Welcome to Escalator Starter"
          ),
          p(
            className := "text-xl text-gray-600 dark:text-gray-400 max-w-2xl",
            "A modern full-stack Scala application template built with Laminar, Tailwind CSS, and the eSCALAtor framework"
          )
        ),

        // Feature cards
        div(
          className := "grid grid-cols-1 md:grid-cols-3 gap-6 mb-12 max-w-4xl",

          featureCard(
            "⚡ Fast Development",
            "Type-safe frontend and backend with shared models"
          ),
          featureCard(
            "🎨 Beautiful UI",
            "Pre-built components with Tailwind CSS styling"
          ),
          featureCard(
            "📦 Ready to Deploy",
            "Production-ready build configuration included"
          )
        ),

        // CTA buttons
        div(
          className := "flex gap-4",
          Button(
            text = "Get Started",
            variant = Button.Variant.Primary,
            size = Button.Size.Large,
            onClickHandler = () => Router.navigateTo(Router.Page.Register)
          ),
          Button(
            text = "Sign In",
            variant = Button.Variant.Outline,
            size = Button.Size.Large,
            onClickHandler = () => Router.navigateTo(Router.Page.Login)
          )
        )
      )
    )
  }

  private def featureCard(title: String, description: String): HtmlElement = {
    Card(
      div(
        className := "text-center p-4",
        h3(
          className := "text-lg font-semibold text-gray-900 dark:text-white mb-2",
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
