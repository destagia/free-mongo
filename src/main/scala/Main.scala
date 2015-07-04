package main
package scala

import Mongo._

object Main {

    def main(args: Array[String]): Unit = {
      execute(mongo)()
    }

    def mongo = for {
      _ <- lift(println("DB Action Start"))
      _ <- insert("tagia0", "Shohei Miyashita", 21)
      _ <- insert("tagia1", "Shohei Miyashita", 21)
      _ <- insert("tagia2", "Shohei Miyashita", 21)
      _ <- insert("tagia3", "Shohei Miyashita", 21)
      opt <- findOneById("tagia0212")
      _ <- lift(println(opt.getOrElse("存在しません")))
    } yield ()

}

