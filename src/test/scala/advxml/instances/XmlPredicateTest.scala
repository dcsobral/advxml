package advxml.instances

import org.scalatest.funsuite.AnyFunSuite

import scala.xml.{Elem, NodeSeq}

class XmlPredicateTest extends AnyFunSuite {

  import advxml.instances.transform.predicates._
  import advxml.syntax.normalize._

  test("Filter by text") {

    val data: Elem =
      <Persons>
        <Person>David</Person>
        <Person>Marco</Person>
        <Person>Simone</Person>
      </Persons>

    val result: NodeSeq = data \ "Person" filter text(_ == "Simone")

    assert(result === <Person>Simone</Person>)
  }

  test("Filter by label") {

    val data: Elem =
      <Persons>
        <Person1>David</Person1>
        <Person2>Marco</Person2>
        <Person3>Simone</Person3>
      </Persons>

    val result: NodeSeq = data.child filter label(_ == "Person3")

    assert(result === <Person3>Simone</Person3>)
  }

  test("Filter by attrs") {

    val data: Elem =
      <Persons>
        <Person A="1" B="A">David</Person>
        <Person A="2" B="B">Marco</Person>
        <Person A="3" B="C">Simone</Person>
      </Persons>

    val result: NodeSeq = data \ "Person" filter attrs(
        "A" -> (_ == "3"),
        "B" -> (_ == "C")
      )

    assert(result === <Person A="3" B="C">Simone</Person>)
  }

  test("Filter by strict equality") {

    val data: Elem =
      <Persons>
        <Person A="1"></Person>
        <Person A="2"></Person>
        <Person A="3"></Person>
      </Persons>

    val result: NodeSeq = data \ "Person" filter strictEqualsTo(<Person A="3"></Person>)

    assert(result === <Person A="3"></Person>)
  }

  test("Filter by strict equality - minimized empty") {

    val data: Elem =
      <Persons>
        <Person A="1"></Person>
        <Person A="2"></Person>
        <Person A="3"></Person>
      </Persons>

    val result: NodeSeq = data \ "Person" filter strictEqualsTo(<Person A="3"/>)

    assert(result === <Person A="3"></Person>)
  }
}
