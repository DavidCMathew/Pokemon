package helpers

import java.sql.{DriverManager, ResultSet}

import com.mysql.jdbc.Driver
import models._


import scala.collection.immutable.ListSet
object Utilities {
  private val url = "jdbc:mysql://localhost/POKEMON"
  private val username = "dave"
  private val password = "dave"
  DriverManager.registerDriver(new Driver)
  private val connection = DriverManager.getConnection(url, username, password)
  def sendquery(query:String):ResultSet= {
    val res=connection createStatement() executeQuery (query)
    res
  }

  def trainersPokemon(trainer:String):ListSet[Poke]={
    val res=sendquery(s"SELECT P.NAME,P.ID,P.TYPE1,P.TYPE2,P.ABILITY,P.EVOLVES_FROM FROM TEAM T,TRAINER TR,POKEMON P WHERE TR.ID=T.TRAINER AND P.ID=T.POKEMON AND TR.NAME='$trainer';")
    var pokes:ListSet[Poke]=new ListSet[Poke]
    while(res.next())
      pokes=pokes+new Poke(res.getString("NAME"),res.getInt("ID"),res.getString("TYPE1")
        ,res.getString("TYPE2"),res.getString("ABILITY"),res.getInt("EVOLVES_FROM"))
    pokes
  }

  def getAbilities(name:String):String={
    val res=sendquery(s"SELECT INFO FROM ABILITIES WHERE NAME='$name';")
    res.next()
    res getString("INFO")
  }

  def findPokemon(p:Poke):ListSet[Poke]={
    val sel:String="SELECT * FROM POKEMON WHERE "

    val nameclause=if (p.name.isEmpty)"" else s"(NAME LIKE '%${p.name}%') AND "

    val idclause = if (p.id!=0) s"(ID='${p.id}') AND " else ""

    val typeclause=if(p.type1.isEmpty&&p.type2.isEmpty)""
                  else if(p.type1.nonEmpty&&(p.type2.nonEmpty))
                                s"(TYPE1='${p.type1}' AND TYPE2='${p.type2}') OR (TYPE1='${p.type2}' AND TYPE2='${p.type1}') AND "
                  else s"(TYPE1='${p.type1}' OR TYPE2='${p.type1}') AND "

    val abilityclause = if(!p.ability.isEmpty) s"(ABILITY='${p.ability}') AND " else ""

    val evolclause = if(p.evolvesFrom!=0) s"EVOLVES_FROM='${p.evolvesFrom}' AND " else ""

    val query=sel+nameclause+idclause+typeclause+abilityclause+evolclause

    val finalquery=if(query.endsWith("WHERE "))query.substring(0,query.lastIndexOf("WHERE"))
    else if (query.endsWith("AND "))query.substring(0,query.lastIndexOf("AND"))
    val res=sendquery(finalquery+";")
    var poke:ListSet[Poke]=new ListSet[Poke]
    while(res.next())
      poke+=new Poke(res.getString("NAME"),res.getInt("ID"),res.getString("TYPE1")
                    ,res.getString("TYPE2"),res.getString("ABILITY"),res.getInt("EVOLVES_FROM"))
    poke
  }

  def getWeakness(t1:String):ListSet[String]={
    val res=sendquery(s"SELECT WEAK_VS FROM MATCHUP WHERE TYPE='${t1}'")
    var weaklist:ListSet[String]=new ListSet[String]
    while(res.next)
      weaklist = weaklist + res.getString("WEAK_VS")
    weaklist
  }

  def getStrength(t1:String):ListSet[String]={
    val res=sendquery(s"SELECT TYPE FROM MATCHUP WHERE WEAK_VS='${t1}'")
    var strlist:ListSet[String]=new ListSet[String]
    while(res.next)
      strlist = strlist + res.getString("TYPE")
    strlist
  }

  def allWeakness(t1:String,t2:String):ListSet[String]={
    val str1=getStrength(t1)-t2
    val weak1=getWeakness(t1)-t2
    val str2=getStrength(t2)-t1
    val weak2=getWeakness(t2)-t1

    weak1 ++ weak2 -- str1 -- str2
  }

  def allStrengths(t1:String,t2:String):ListSet[String]={
    val str1=getStrength(t1)
    val weak1=getWeakness(t1)
    val str2=getStrength(t2)
    val weak2=getWeakness(t2)

    str1 ++ str2 -- weak1 -- weak2
  }


}
