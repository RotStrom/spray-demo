package com.github.rotstrom.spray.demo.api

import java.nio.file.{Files, Path, Paths}

import akka.actor.{Actor, ActorRefFactory}
import akka.util.Timeout
import com.github.rotstrom.spray.demo.model.{Cat, Pets}
import play.api.libs.json.{JsValue, Json}
import spray.httpx.PlayJsonSupport
import spray.routing.{HttpService, Route}

import scala.concurrent.duration._

class PetServiceActor extends Actor with PetService {
  override implicit def actorRefFactory: ActorRefFactory = context

  implicit val system = context.system

  override def receive: Receive = runRoute(route)
}

trait PetService extends HttpService
  with PlayJsonSupport
  with CatsComponent
  with PetsComponentImpl {

  private implicit val timeout = Timeout(5.seconds)
  private implicit val printer: JsValue ⇒ String = Json.prettyPrint

  lazy val storage = new PetsStorageImpl(Paths.get("/tmp/foo/pets.json"))
  lazy val catsDAO = new CatsDAOImpl

  lazy val catRoute: Route =
    pathPrefix("cat") {
      get {
        path(IntNumber)(id ⇒ complete(catsDAO.read(id.toString))) ~
          pathEnd(complete(catsDAO.all))
      } ~
        (post & pathEnd) {
          extract(ctx ⇒ Json.parse(ctx.request.entity.asString)) { json ⇒
            complete {
              val name = (json \ "name").as[String]
              catsDAO.create(name)
            }
          }
        } ~
        put {
          path(IntNumber) { id ⇒
            extract(ctx ⇒ Json.parse(ctx.request.entity.asString).as[Cat]) { cat ⇒
              complete(catsDAO.update(cat))
            }
          }
        }
    }

  lazy val route: Route =
    pathPrefix("pet") {
      catRoute
    }
}


trait PetsComponentImpl extends PetsComponent {
  class PetsStorageImpl(path: Path) extends PetsStorage {
    def getPets: Pets = Json.parse(Files.readAllBytes(path)).as[Pets]
    def savePets(pets: Pets): Unit =
      Files.write(
        path,
        Json.prettyPrint(Json.toJson(pets)).getBytes
      )
  }
}

trait PetsComponent {
  val storage: PetsStorage

  trait PetsStorage {
    def getPets: Pets
    def savePets(pets: Pets): Unit
  }
}

trait DAO[T] {
  def all: List[T]
  def create(name: String): T
  def read(id: String): Option[T]
  def update(cat: T): T
  def delete(id: String): Unit
}

trait CatsComponent { 
  this: PetsComponent ⇒

  class CatsDAOImpl extends DAO[Cat] {
    def all: List[Cat] = storage.getPets.cats.values.toList

    def create(name: String): Cat = {
      val pets = storage.getPets
      val newCat = Cat(pets.cats.keys.size + 1, name, None)
      storage.savePets(pets.copy(cats = pets.cats + (newCat.id.toString → newCat)))
      newCat
    }

    def read(id: String): Option[Cat] = {
      storage.getPets.cats.get(id.toString)
    }

    def update(cat: Cat): Cat = {
      val pets = storage.getPets
      storage.savePets(pets.copy(cats = pets.cats + (cat.id.toString → cat)))
      cat
    }

    def delete(id: String): Unit = {
      val pets = storage.getPets
      storage.savePets(pets.copy(cats = pets.cats - id.toString))
    }
  }
}
