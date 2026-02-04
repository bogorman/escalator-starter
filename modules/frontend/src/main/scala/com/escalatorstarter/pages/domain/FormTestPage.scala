package com.escalatorstarter.pages.domain

import com.raquo.laminar.api.L._
import escalator.frontend.components.forms._
import escalator.frontend.components.ui.ToastService
import org.scalajs.dom

/**
  * Test page for demonstrating Form components
  */
object FormTestPage {

  def apply(): HtmlElement = {

    div(
      className := "min-h-screen bg-gray-50 dark:bg-gray-900 p-8",

      // Header
      div(
        className := "mb-8",
        h1(
          className := "text-3xl font-bold text-gray-900 dark:text-white mb-2",
          "Form Components Test Page"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400",
          "Interactive demonstration of Form Builder and SimpleForm trait"
        )
      ),

      // SimpleForm Trait Example
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "SimpleForm Trait - Login Form"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Using SimpleForm trait mixed into an object"
        ),
        LoginForm.render()
      ),

      // FormBuilder Example - Registration
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "FormBuilder - Registration Form"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Declarative form builder with validation"
        ),
        FormBuilder(
          fields = List(
            FieldDef(
              name = "username",
              label = "Username",
              fieldType = FormFieldType.Text,
              validators = List(
                FormValidation.required(),
                FormValidation.minLength(3, Some("Username must be at least 3 characters"))
              ),
              placeholder = Some("Enter username"),
              helpText = Some("Your unique username")
            ),
            FieldDef(
              name = "email",
              label = "Email",
              fieldType = FormFieldType.Email,
              validators = List(
                FormValidation.required(),
                FormValidation.email()
              ),
              placeholder = Some("Enter email")
            ),
            FieldDef(
              name = "password",
              label = "Password",
              fieldType = FormFieldType.Password,
              validators = List(
                FormValidation.required(),
                FormValidation.minLength(8, Some("Password must be at least 8 characters"))
              ),
              placeholder = Some("Enter password"),
              helpText = Some("Minimum 8 characters")
            ),
            FieldDef(
              name = "bio",
              label = "Bio",
              fieldType = FormFieldType.TextArea,
              validators = List(
                FormValidation.maxLength(500)
              ),
              placeholder = Some("Tell us about yourself"),
              helpText = Some("Max 500 characters")
            )
          ),
          onSubmitCallback = data => {
            dom.console.log(s"Registration submitted: ${data.values}")
            ToastService.success(s"Registration successful for ${data("username")}!")
          },
          onError = () => {
            ToastService.error("Please fix validation errors")
          },
          submitButtonText = "Register",
          showCancelButton = true,
          cancelButtonText = "Reset"
        )
      ),

      // Profile Form Example
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Profile Form - All Field Types"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Demonstrating all form field types"
        ),
        FormBuilder(
          fields = List(
            FieldDef(
              name = "name",
              label = "Full Name",
              fieldType = FormFieldType.Text,
              validators = List(FormValidation.required()),
              placeholder = Some("John Doe")
            ),
            FieldDef(
              name = "email",
              label = "Email",
              fieldType = FormFieldType.Email,
              validators = List(FormValidation.required(), FormValidation.email()),
              placeholder = Some("john@example.com")
            ),
            FieldDef(
              name = "phone",
              label = "Phone",
              fieldType = FormFieldType.Tel,
              validators = List(),
              placeholder = Some("+1 (555) 123-4567")
            ),
            FieldDef(
              name = "website",
              label = "Website",
              fieldType = FormFieldType.Url,
              validators = List(),
              placeholder = Some("https://example.com")
            ),
            FieldDef(
              name = "age",
              label = "Age",
              fieldType = FormFieldType.Number,
              validators = List(
                FormValidation.required(),
                FormValidation.range(18, 120, Some("Age must be between 18 and 120"))
              ),
              placeholder = Some("25")
            ),
            FieldDef(
              name = "birthdate",
              label = "Birth Date",
              fieldType = FormFieldType.Date,
              validators = List()
            ),
            FieldDef(
              name = "country",
              label = "Country",
              fieldType = FormFieldType.Select(List(
                ("us", "United States"),
                ("uk", "United Kingdom"),
                ("ca", "Canada"),
                ("au", "Australia")
              )),
              validators = List(FormValidation.required())
            ),
            FieldDef(
              name = "bio",
              label = "Bio",
              fieldType = FormFieldType.TextArea,
              validators = List(FormValidation.maxLength(200)),
              placeholder = Some("Tell us about yourself...")
            )
          ),
          onSubmitCallback = data => {
            dom.console.log(s"Profile updated: ${data.values}")
            ToastService.success("Profile updated successfully!")
          },
          submitButtonText = "Save Profile",
          showCancelButton = true
        )
      ),

      // Contact Form Example
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Contact Form - SimpleForm Trait"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Another example using SimpleForm trait"
        ),
        ContactForm.render()
      ),

      // Inline Form Example
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Inline Form - Search/Filter"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Compact inline form for search and filters"
        ),
        FormBuilder.inline(
          fields = List(
            FieldDef(
              name = "search",
              label = "Search",
              fieldType = FormFieldType.Text,
              placeholder = Some("Search...")
            ),
            FieldDef(
              name = "category",
              label = "Category",
              fieldType = FormFieldType.Select(List(
                ("all", "All"),
                ("tech", "Technology"),
                ("business", "Business"),
                ("health", "Health")
              ))
            )
          ),
          onSubmitCallback = data => {
            dom.console.log(s"Search: ${data.values}")
            ToastService.info(s"Searching for '${data("search")}' in ${data("category")}")
          },
          submitButtonText = "Search"
        )
      ),

      // Custom Validators Example
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Custom Validators"
        ),
        p(
          className := "text-gray-600 dark:text-gray-400 mb-4",
          "Form with custom validation rules"
        ),
        CustomValidatorForm.render()
      ),

      // Usage Instructions Section
      div(
        className := "mb-8 bg-white dark:bg-gray-800 rounded-lg shadow p-6",
        h2(
          className := "text-2xl font-semibold text-gray-900 dark:text-white mb-4",
          "Usage Instructions"
        ),
        div(
          className := "text-sm text-gray-600 dark:text-gray-400 space-y-2",

          p(className := "font-semibold", "1. Using SimpleForm Trait:"),
          pre(
            className := "bg-gray-100 dark:bg-gray-700 p-3 rounded mt-2 overflow-x-auto",
            code(
              """object MyForm extends SimpleForm {
  val emailField = addField("email", "", List(required, email))
  val passwordField = addField("password", "", List(required, minLength(8)))

  def render = div(
    renderField("email", "Email", FormFieldType.Email),
    renderField("password", "Password", FormFieldType.Password),
    renderSubmitButton("Submit", handleSubmit)
  )

  def handleSubmit(data: FormData): Unit = {
    // Handle submission
    ToastService.success("Submitted!")
    setSubmitting(false)
  }
}"""
            )
          ),

          p(className := "font-semibold mt-4", "2. Using FormBuilder:"),
          pre(
            className := "bg-gray-100 dark:bg-gray-700 p-3 rounded mt-2 overflow-x-auto",
            code(
              """FormBuilder(
  fields = List(
    FieldDef("email", "Email", FormFieldType.Email,
      validators = List(required, email)),
    FieldDef("password", "Password", FormFieldType.Password,
      validators = List(required, minLength(8)))
  ),
  onSubmit = data => ToastService.success("Success!"),
  submitButtonText = "Submit"
)"""
            )
          ),

          p(className := "font-semibold mt-4", "3. Available Validators:"),
          ul(
            className := "list-disc list-inside mt-2 space-y-1",
            li("required() - Field must not be empty"),
            li("email() - Valid email format"),
            li("minLength(n) - Minimum character count"),
            li("maxLength(n) - Maximum character count"),
            li("pattern(regex) - Match regex pattern"),
            li("range(min, max) - Numeric range"),
            li("min(value) - Minimum numeric value"),
            li("max(value) - Maximum numeric value"),
            li("matches(value) - Match another field"),
            li("custom(predicate, msg) - Custom validation logic")
          )
        )
      )
    )
  }
}

/**
  * Login Form using SimpleForm trait
  */
object LoginForm extends SimpleForm {
  import FormValidation._

  // Define fields
  val emailField = addField("email", "", List(required(), email()))
  val passwordField = addField("password", "", List(required(), minLength(8)))

  def render(): HtmlElement = {
    div(
      className := "max-w-md",
      renderField("email", "Email", FormFieldType.Email, placeholder = Some("Enter your email")),
      renderField("password", "Password", FormFieldType.Password, placeholder = Some("Enter password")),
      div(
        className := "flex gap-3",
        renderSubmitButton("Login", handleLogin)
      )
    )
  }

  def handleLogin(data: FormData): Unit = {
    dom.console.log(s"Login attempt: ${data.values}")
    ToastService.success(s"Welcome back, ${data("email")}!")
    setSubmitting(false)
    resetForm()
  }
}

/**
  * Contact Form using SimpleForm trait
  */
object ContactForm extends SimpleForm {
  import FormValidation._

  // Define fields
  val nameField = addField("name", "", List(required(), minLength(2)))
  val emailField = addField("email", "", List(required(), email()))
  val subjectField = addField("subject", "", List(required()))
  val messageField = addField("message", "", List(required(), minLength(10), maxLength(500)))

  def render(): HtmlElement = {
    div(
      className := "max-w-md",
      renderField("name", "Name", FormFieldType.Text, placeholder = Some("Your name")),
      renderField("email", "Email", FormFieldType.Email, placeholder = Some("your@email.com")),
      renderField("subject", "Subject", FormFieldType.Text, placeholder = Some("What's this about?")),
      renderField("message", "Message", FormFieldType.TextArea,
        placeholder = Some("Your message..."),
        helpText = Some("10-500 characters")),
      div(
        className := "flex gap-3",
        renderCancelButton(),
        renderSubmitButton("Send Message", handleSubmit)
      )
    )
  }

  def handleSubmit(data: FormData): Unit = {
    dom.console.log(s"Contact form: ${data.values}")
    ToastService.success("Message sent successfully!")
    setSubmitting(false)
    resetForm()
  }
}

/**
  * Form with custom validators
  */
object CustomValidatorForm extends SimpleForm {
  import FormValidation._

  // Custom validator for username
  val usernameValidator = custom[String](
    value => value.matches("^[a-zA-Z0-9_]+$"),
    "Username can only contain letters, numbers, and underscores"
  )

  // Custom validator for strong password
  val strongPasswordValidator = custom[String](
    value => value.exists(_.isUpper) && value.exists(_.isLower) && value.exists(_.isDigit),
    "Password must contain uppercase, lowercase, and numbers"
  )

  val usernameField = addField("username", "", List(required(), minLength(3), usernameValidator))
  val passwordField = addField("password", "", List(required(), minLength(8), strongPasswordValidator))
  val confirmPasswordField = addField("confirmPassword", "", List(required()))

  def render(): HtmlElement = {
    div(
      className := "max-w-md",
      renderField("username", "Username", FormFieldType.Text,
        placeholder = Some("johndoe123"),
        helpText = Some("Letters, numbers, and underscores only")),
      renderField("password", "Password", FormFieldType.Password,
        placeholder = Some("Enter strong password"),
        helpText = Some("Must include uppercase, lowercase, and numbers")),
      renderField("confirmPassword", "Confirm Password", FormFieldType.Password,
        placeholder = Some("Confirm password")),
      renderSubmitButton("Create Account", handleSubmit)
    )
  }

  def handleSubmit(data: FormData): Unit = {
    // Check password match
    if (data("password") != data("confirmPassword")) {
      ToastService.error("Passwords do not match")
      setSubmitting(false)
      return
    }

    dom.console.log(s"Account created: ${data.values}")
    ToastService.success(s"Account created for ${data("username")}!")
    setSubmitting(false)
    resetForm()
  }
}
