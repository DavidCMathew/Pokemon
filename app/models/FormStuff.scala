package models

import play.api.data.{Form, Forms}

object FormStuff {
  case class poke(n:String,t1:String,t2:String,ab:String)

  val pokeForm = Form(Forms.mapping(
    "n"->Forms.text,
    "t1"-> Forms.text,
    "t2"->Forms.text,
    "ab"->Forms.text
    )(poke.apply)(poke.unapply)
  )

  case class pokefull(n:String,id:Int,t1:String,t2:String,ab:String,ef:Int)
  val fullpokeForm = Form(Forms.mapping(
    "n"->Forms.text,
    "id"->Forms.number,
    "t1"-> Forms.text,
    "t2"->Forms.text,
    "ab"->Forms.text,
    "ef"->Forms.number
    )(pokefull.apply)(pokefull.unapply)
  )

}
