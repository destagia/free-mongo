package main.scala

import com.mongodb._
import monad._

sealed trait Mongo[A] {
  def exec(collection: DBCollection): () => A
}
case class FindOneById(id: String) extends Mongo[Option[DBObject]] {
  def exec(c: DBCollection) = () => {
    val obj = new BasicDBObject()
    obj.put("id", id)
    Some(c.findOne(obj))
  }
}
case class Insert(id: String, name: String, age: Int) extends Mongo[Unit] {
  def exec(c: DBCollection) = () => {
    val obj =
      new BasicDBObject()
      .append("id", id).append("name", name).append("age", age)
    c.insert(obj)
  }
}

object Mongo {
  import Free._
  import Monad._
  type MongoF[A] = Free[Mongo, A]

  def Lift[A](a: A): Mongo[A] = new Mongo[A] {
    def exec(c: DBCollection) = () => a
  }

  def client = new MongoClient(new ServerAddress("localhost", 27017))
  def db(dbName: String) = client.getDB(dbName)
  def collection(dbName: String, collName: String) =
    db(dbName).getCollection(collName)

  def findOneById(id: String): MongoF[Option[DBObject]] =
    Suspend(FindOneById(id))
  def insert(id: String, name: String, age: Int): MongoF[Unit] =
    Suspend(Insert(id, name, age))
  def lift[A](a: A): MongoF[A] =
    Suspend(Lift(a))

  val mongoToFunction0 = new (Mongo ~> Function0) {
    def apply[A](m: Mongo[A]) = m.exec(collection("hoge", "foo"))
  }

  def execute[A](f: MongoF[A]) =
    runFree(f)(mongoToFunction0)

}