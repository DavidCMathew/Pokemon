package helpers

import java.sql.{DriverManager, ResultSet}

import com.mysql.jdbc.Driver
import models._


import scala.collection.immutable.ListSet
object Utilities {
  def sendquery(query:String):ResultSet= {

    val url = "jdbc:mysql://localhost/POKEMON"
    val username = "dave"
    val password = "dave"
    DriverManager.registerDriver(new Driver)
    val connection = DriverManager.getConnection(url, username, password)
    connection createStatement() executeQuery (query)

  }

  def trainersPokemon(trainer:String):ListSet[String]={
    val res=sendquery(s"SELECT P.NAME FROM TEAM T,TRAINER TR,POKEMON P WHERE TR.ID=T.TRAINER AND P.ID=T.POKEMON AND TR.NAME='$trainer';")
    var pokes:ListSet[String]=new ListSet[String]
    while(res.next())
      pokes=pokes+res.getString("NAME")
    pokes
  }

  def getAbilities(name:String):String={
    val res=sendquery(s"SELECT INFO FROM ABILITIES WHERE NAME='$name';")
    res.next()
    res getString("INFO")
  }

  def findPokemon(p:Poke):ListSet[Poke]={
    val sel:String="SELECT * FROM POKEMON WHERE "

    val nameclause=if (p.name.isEmpty)"" else s"NAME='${p.name}' AND "

    val typeclause=if(p.type1.isEmpty&&p.type2.isEmpty)""
                  else if(p.type1.nonEmpty&&(p.type2.nonEmpty))
                                s"(TYPE1='${p.type1}' AND TYPE2='${p.type2}') OR (TYPE1='${p.type2}' AND TYPE2='${p.type1}') AND "
                  else s"TYPE1='${p.type1}' OR TYPE2='${p.type1}' AND "
    val query=sel+nameclause+typeclause
    val finalquery=if(query.endsWith("WHERE "))query.substring(0,query.lastIndexOf("WHERE"))
    else if (query.endsWith("AND "))query.substring(0,query.lastIndexOf("AND"))
    val res=sendquery(finalquery+";")
    var poke:ListSet[Poke]=new ListSet[Poke]
    while(res.next())
      poke+=new Poke(res.getString("NAME"),res.getInt("ID"),res.getString("TYPE1")
                    ,res.getString("TYPE2"),res.getString("ABILITY"),res.getInt("EVOLVES_FROM"))
    poke
  }
}
