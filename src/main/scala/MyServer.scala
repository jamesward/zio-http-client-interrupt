import zio.*
import zio.stm.*
import zio.http.*
import zio.http.model.{Method, Status}


object MyServer extends ZIOAppDefault:

  val app = Http.collectZIO[Request] {
    case Method.GET -> Path.root =>
      ZIO.sleep(10.seconds).map(_ => Response.text("done")).onExit { e =>
        Console.printLine(e).exit
      }
  }

  def run =
    Server.serve(app).provide(Server.default)
