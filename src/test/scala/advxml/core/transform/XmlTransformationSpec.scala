package advxml.core.transform

import advxml.core.transform.actions.XmlZoom
import advxml.test.generators.XmlGenerator
import advxml.test.generators.XmlGenerator.XmlGeneratorConfig
import org.scalacheck.{Arbitrary, Properties}
import org.scalacheck.Prop.forAll

import scala.util.Try
import scala.xml.{Elem, Node, NodeSeq}

/**
  * Advxml
  * Created by geirolad on 12/07/2019.
  *
  * @author geirolad
  */
object XmlTransformationSpec extends Properties("XmlTransformationSpec") {

  //noinspection RedundantDefaultArgument
  implicit val xmlGenConfig: XmlGeneratorConfig = XmlGeneratorConfig(
    childMaxSize = 1,
    attrsMaxSize = 1,
    attrsMaxNameSize = 3
  )

  implicit val elemGenerator: Arbitrary[Elem] = Arbitrary(
    XmlGenerator
      .xmlElemGenerator()
      .filter(_.children.nonEmpty)
      .map(_.toElem)
  )

  import advxml.implicits._
  import cats.instances.option._
  import cats.instances.try_._

  property("Prepend") = forAll { (base: Elem, newElem: Elem) =>
    val zoom: XmlZoom = XmlGenerator.xmlZoomGenerator(base).sample.get
    val rule: ComposableXmlRule = zoom ==> Prepend(newElem)
    val result: Try[NodeSeq] = base.transform[Try](rule)
    val targetUpdated: NodeSeq = result.toOption
      .flatMap(zoom(_))
      .map(_.node)
      .get

    (targetUpdated \ newElem.label).nonEmpty
  }

  property("Append") = forAll { (base: Elem, newElem: Elem) =>
    val zoom: XmlZoom = XmlGenerator.xmlZoomGenerator(base).sample.get
    val rule: ComposableXmlRule = zoom ==> Append(newElem)
    val result: Try[NodeSeq] = base.transform[Try](rule)
    val targetUpdated: NodeSeq = result.toOption
      .flatMap(zoom(_))
      .map(_.node)
      .get

    (targetUpdated \ newElem.label).nonEmpty
  }

  property("Replace") = forAll { (base: Elem, newElem: Elem) =>
    val zoom: XmlZoom = XmlGenerator.xmlZoomGenerator(base).sample.get
    val rule: ComposableXmlRule = zoom ==> Replace(_ => newElem)
    val result: Try[NodeSeq] = base.transform[Try](rule)

    result.toOption.flatMap(zoom(_)).isEmpty
  }

  property("Remove") = forAll { base: Elem =>
    val zoom: XmlZoom = XmlGenerator.xmlZoomGenerator(base).sample.get
    val rule: FinalXmlRule = zoom ==> Remove
    val result: Option[Node] = base.transform[Try](rule).get.headOption

    result.flatMap(zoom(_)).isEmpty
  }
}
