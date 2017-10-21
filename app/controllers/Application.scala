package controllers

import play.api._
import play.api.mvc._
import helpers.Utilities._
import models.Poke

import scala.collection.immutable.ListSet

class Application extends Controller {


  def home = Action {
    Ok(views.html.home())
  }


  def addTrainer = Action{
    Ok(views.html.addnew())
  }

  def viewTrainers = Action{
    val res=sendquery("SELECT NAME FROM TRAINER")
    var trainernames:ListSet[String]=new ListSet[String]
    while (res.next())
      trainernames=trainernames+res.getString("NAME")
    var A:Map[String,ListSet[String]] = Map()
    for(x<-trainernames)
      A+=(x->trainersPokemon(x))

    A.keys.foreach(p=>{
      println(p+":"+A(p).mkString(" "))
    })
    Ok(views.html.trainers(A))
  }

  def viewAbilities = Action{
    val res=sendquery("SELECT NAME FROM ABILITIES")
    var abilityNames:ListSet[String]=new ListSet[String]
    while (res.next())
      abilityNames=abilityNames+res.getString("NAME")
    var A:Map[String,String] = Map()
    for(x<-abilityNames)
      A+=(x->getAbilities(x))

    A.keys.foreach(p=>{
      println(p+":"+A(p))
    })
    Ok(views.html.abilities(A))
  }

  def viewPokemon=Action{
    val P:Poke=new Poke("",0,"PSYCHIC","","",0)
    val poke=findPokemon(P)
    Ok(views.html.index(poke))
  }
}
