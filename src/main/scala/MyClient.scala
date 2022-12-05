import zio.*
import zio.http.*
import zio.http.model.{Method, Status}
import zio.stm.*

import java.util.concurrent.TimeoutException

object MyClient extends ZIOAppDefault:

  def run =
    val req = for
      resp <- Client.request("http://localhost:8080")
      body <- resp.body.asString
    yield
      body

    val reqWithTimeout = req.timeoutFail(TimeoutException())(5.seconds)

    val reqWithInterrupt = for
      f <- req.fork
      _ <- ZIO.sleep(1.second)
      _ <- f.interrupt
      body <- f.join
    yield
      body

    val reqs = Seq(req, reqWithTimeout, reqWithInterrupt)
    ZIO.collectAllSuccessesPar(reqs).debug.provide(Client.default, Scope.default)
