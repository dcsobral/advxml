package advxml.syntax

import advxml.core.convert.ValidatedConverter
import advxml.core.convert.xml.{ModelToXml, XmlToModel}
import advxml.core.validate.ValidatedNelEx
import cats.data.Validated.Valid
import org.scalatest.funsuite.AnyFunSuite

import scala.xml.Elem

/**
  * Advxml
  * Created by geirolad on 28/06/2019.
  *
  * @author geirolad
  */
//TODO: Check duplication into advxml.core.convert.xml.XmlConverterTest
class XmlConverterSyntaxTest extends AnyFunSuite {

  import advxml.instances.validate._
  import advxml.syntax.convert._
  import advxml.syntax.traverse.validated._
  import cats.instances.option._
  import cats.syntax.all._

  test("XML to Model - Convert simple case class") {

    case class Person(name: String, surname: String, age: Option[Int])

    implicit val converter: XmlToModel[Elem, Person] = ValidatedConverter.of(x => {
      (
        x \@! "Name",
        x \@! "Surname",
        x.\@?("Age").map(_.toInt).valid
      ).mapN(Person)
    })

    val xml = <Person Name="Matteo" Surname="Bianchi"/>
    val res: ValidatedNelEx[Person] = xml.as[Person]

    assert(res.map(_.name) == Valid("Matteo"))
    assert(res.map(_.surname) == Valid("Bianchi"))
  }

  test("Model to XML - Convert simple case class") {

    case class Person(name: String, surname: String, age: Option[Int])

    implicit val converter: ModelToXml[Person, Elem] = ValidatedConverter.of(x =>
      Valid(
        <Person Name={x.name} Surname={x.surname} Age={x.age.map(_.toString).getOrElse("")}/>
      )
    )

    val p = Person("Matteo", "Bianchi", Some(23))
    val res: ValidatedNelEx[Elem] = p.as[Elem]

    assert(res == Valid(<Person Name="Matteo" Surname="Bianchi" Age="23"/>))
  }
}
