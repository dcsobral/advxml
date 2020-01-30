package advxml.syntax

import advxml.core.convert.ValidatedConverter
import advxml.core.convert.xml.{ModelToXml, XmlToModel}
import advxml.core.validate.ValidatedEx
import cats.data.Validated.Valid
import org.scalatest.funsuite.AnyFunSuite

import scala.xml.Elem

/**
  * Advxml
  * Created by geirolad on 28/06/2019.
  *
  * @author geirolad
  */
class XmlConverterSyntaxTest extends AnyFunSuite {

  import advxml.syntax.convert._
  import advxml.syntax.nestedMap._
  import advxml.syntax.traverse.validated._
  import cats.implicits._

  test("XML to Model - Convert simple case class") {

    case class Person(name: String, surname: String, age: Option[Int])

    implicit val converter: XmlToModel[Elem, Person] = ValidatedConverter.of(x => {
      (
        x \@! "Name",
        x \@! "Surname",
        x \@? "Age" nestedMap (_.toInt)
      ).mapN(Person)
    })

    val xml = <Person Name="Matteo" Surname="Bianchi"/>
    val res: ValidatedEx[Person] = xml.as[Person]

    assert(res.map(_.name) == Valid("Matteo"))
    assert(res.map(_.surname) == Valid("Bianchi"))
  }

  test("Model to XML - Convert simple case class") {

    case class Person(name: String, surname: String, age: Option[Int])

    implicit val converter: ModelToXml[Person, Elem] = ValidatedConverter.of(
      x =>
        Valid(
          <Person Name={x.name} Surname={x.surname} Age={x.age.map(_.toString).getOrElse("")}/>
        )
    )

    val p = Person("Matteo", "Bianchi", Some(23))
    val res: ValidatedEx[Elem] = p.as[Elem]

    assert(res == Valid(<Person Name="Matteo" Surname="Bianchi" Age="23"/>))
  }
}