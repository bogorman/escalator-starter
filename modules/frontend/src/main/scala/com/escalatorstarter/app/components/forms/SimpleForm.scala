package frontend.app.components.forms

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}
import com.raquo.airstream.eventbus.{EventBus, WriteBus}
// import com.raquo.airstream.eventstream.EventStream
import com.raquo.airstream.core.EventStream
import com.raquo.laminar.api.L._
import escalator.errors.BackendError
import escalator.validators.FieldsValidator
import escalator.streams.sinks.WriteToObserver._
import escalator.streams.sources.ReadFromObservable._
import escalator.syntax.WithUnit

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait SimpleForm[FormData] {

  protected implicit val owner: Owner = new Owner {}

  type FormDataChanger = FormData => FormData

  implicit val formDataWithUnit: WithUnit[FormData]
  implicit val actorSystem: ActorSystem
  protected implicit def ec: ExecutionContext = actorSystem.dispatcher

  lazy val formData: Var[FormData] = Var(formDataWithUnit.unit) // lazy to avoid problems with missing formDataWithUnit
  private val formDataBus = new EventBus[FormData]()
  formDataBus.events.foreach(data => formData.update(_ => data))
  private val errors = new EventBus[Map[String, List[BackendError]]]()
  val $errors: EventStream[Map[String, List[BackendError]]] = errors.events // expose to kids
  val errorsWriter: WriteBus[Map[String, List[BackendError]]] = errors.writer // expose to kids

  private val formDataChanger: EventBus[FormDataChanger] = new EventBus[FormDataChanger]()
  private val formDataChangerWriter: WriteBus[FormDataChanger] = formDataChanger.writer

  /**
    * Allows to concretely make changes to the formData.
    */
  def createFormDataChanger[T](f: T => FormDataChanger): WriteBus[T] =
    formDataChangerWriter.contramapWriter(f)

  private val formDataEventWriter: WriteBus[FormData] = formDataBus.writer

  val validator: FieldsValidator[FormData, BackendError]

  private val formDataSink = Sink.writeToObserver(formDataEventWriter)
  private val errorsSink = Sink.foreach(errorsWriter.onNext)

  private val debugSink = Flow[FormData]
    .filter(_ => scala.scalajs.LinkingInfo.developmentMode)
    .to(
      Sink.foreach(println)
    )

  private val formSource: RunnableGraph[NotUsed] = Source
    .readFromObservable(formDataChanger.events)
    .scan(formDataWithUnit.unit) { case (form, changer) => changer(form) }
    .alsoTo(debugSink)
    .alsoTo(formDataSink)
    .groupedWithin(10, 200.milliseconds)
    .map(_.last)
    .map(validator.validate)
    .wireTap(errors => {
      println(s"The errors pass: ${errors.size} errors")
      println(errors)
    })
    .to(errorsSink)

  final def run(): Unit = formSource.run()

  final def clearForm(): Unit =
    formDataChangerWriter.onNext(_ => formDataWithUnit.unit)

  final def setFormData(data: FormData): Unit =
    formDataChangerWriter.onNext(_ => data)

  def filterErrors(matching: List[String]): EventStream[List[BackendError]] = {
    $errors.map { m =>
      // matching.contains(i._1)
      m.filter { i => 
        matching.contains(i._1)
      }.flatMap(_._2).toList
    }
  }

  def firstMatchingErrors(matching: List[String]): EventStream[Option[BackendError]] = {
    filterErrors(matching).map { m => m.headOption }
  }

  def firstMatchingCheckedError(check: EventStream[Boolean], matching: List[String]): EventStream[Option[BackendError]] = {
    val filteredErrors: EventStream[Option[BackendError]] = filterErrors(matching).map { m => m.headOption }
    filteredErrors.combineWith(check).map { 
      case (beOpt,true) => {
        beOpt 
      }
      case _ => {
        None
      }
    }
  }

  // def firstMatchingCheckedError(check: Signal[Boolean], matching: List[String]): EventStream[Option[BackendError]] = {
  //   val filteredErrors: EventStream[Option[BackendError]] = filterErrors(matching).map { m => m.headOption }
  //   filteredErrors.combineWith(check).map { 
  //     case (beOpt,true) => {
  //       beOpt 
  //     }
  //     case _ => {
  //       None
  //     }
  //   }
  // }


}
