package main.scala.monad

import language.higherKinds

trait Functor[F[_]] {
  def map[A,B](a: F[A])(f: A => B): F[B]
}

trait Monad[F[_]] extends Functor[F] {
  def unit[A](a: => A): F[A]
  def flatMap[A,B](a: F[A])(f: A => F[B]): F[B]

  def map[A,B](a: F[A])(f: A => B): F[B] = flatMap(a)(a => unit(f(a)))
  def map2[A,B,C](a: F[A], b: F[B])(f: (A,B) => C): F[C] =
    flatMap(a)(a => map(b)(b => f(a,b)))
}

object Monad {

  implicit def freeMonad[F[_]] = {
    type λ[a] = Free[F, a]
    new Monad[λ] {
      def unit[A](a: => A): λ[A] = Return(a)
      def flatMap[A,B](a: λ[A])(f: A => λ[B]): λ[B] = FlatMap(a, f)
    }
  }

  implicit val function0Monad = new Monad[Function0] {
    def unit[A](a: => A) = () => a
    def flatMap[A,B](a: Function0[A])(f: A => Function0[B]) =
      () => f(a())()
  }

}
