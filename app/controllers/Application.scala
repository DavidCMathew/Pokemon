package controllers

import play.api.mvc._
import helpers.Utilities._
import models.Poke
import play.api.data.Form
import play.api.Play.current
import models.FormStuff._
import play.api.i18n.Messages.Implicits._

import scala.collection.immutable.ListSet

class Application extends Controller {

  def home = Action {
    Ok(views.html.home())
  }

  def viewTrainers = Action{
    val res=sendquery("SELECT NAME FROM TRAINER")
    var trainernames:ListSet[String]=new ListSet[String]
    while (res.next())
      trainernames=trainernames+res.getString("NAME")

    var X:Map[String,ListSet[Poke]] = Map[String,ListSet[Poke]]()
    for(t<-trainernames)
      X=X.+(t->trainersPokemon(t))

    Ok(views.html.trainers(X))
  }

  def viewAbilities = Action{

    var X:Map[String,String]=Map[String,String]()
    val res = sendquery("SELECT * FROM ABILITIES")
    while (res.next)
      X=X.+(res.getString("NAME")->res.getString("INFO"))
    Ok(views.html.abilities(X))
  }

  def viewPokemon()=Action{
    val poke=findPokemon(new Poke("",0,"","","",0))
    Ok(views.html.index(poke))
  }

  def singlePokemon(id:Int)=Action{
    val p=findPokemon(new Poke("",id,"","","",0)).head

    val prev=if(p.evolvesFrom!=0)findPokemon(new Poke("",p.evolvesFrom,"","","",0)).head else new Poke("",0,"","","",0)
    val next=findPokemon(new Poke("",0,"","","",id))
    val strengths = if(p.type2==null) getStrength(p.type1) else allStrengths(p.type1,p.type2)
    val weakness = if(p.type2==null) getWeakness(p.type1) else allWeakness(p.type1,p.type2)
    Ok(views.html.poke(p,prev,next,strengths,weakness))
  }

  def findPoke=Action{ implicit request =>

    Ok(views.html.search(pokeForm))
  }


  def findres2 = Action { implicit request=>
    val errorFunction = { formWithErrors: Form[poke] =>

      BadRequest(views.html.home())
    }

    val successFunction = { data: poke =>

      val P=new Poke(data.n.toUpperCase().trim,0,data.t1.toUpperCase().trim,data.t2.toUpperCase().trim,data.ab.toUpperCase().trim,0)
      val search=findPokemon(P)

      Ok(views.html.index(search))
    }

    val formValidationResult = pokeForm.bindFromRequest
    formValidationResult.fold(errorFunction, successFunction)
  }

  def singleabi(s:String) = Action{
    var X:Map[String,String]=Map[String,String]()
    val res = sendquery("SELECT * FROM ABILITIES")
    while (res.next)
      X=X.+(res.getString("NAME")->res.getString("INFO"))
    Ok(views.html.abilities(X,s))
  }

  def addPoke()=Action {implicit request=>
    Ok(views.html.addPoke(fullpokeForm))
  }

  def addPokeHandler()=Action{implicit request=>
    val errorFunction = { formWithErrors: Form[pokefull] =>

      Ok(views.html.addPoke(formWithErrors,true))
    }

    val successFunction = { data: pokefull =>

      val name:String = data.n.trim.toUpperCase
      val id:Int = data.id
      val ability:String = data.ab.trim.toUpperCase
      val type1:String = data.t1.trim.toUpperCase
      val type2:String = if(data.t2.isEmpty) "NULL" else s"'${data.t2.trim.toUpperCase}'"
      val evolves = if(data.ef==0) "NULL" else data.ef
      try {
        sendUpdate(s"INSERT INTO POKEMON VALUES ($id,'$name','$type1',$type2,'$ability',$evolves);")
        val res = findPokemon(new Poke("", id, "", "", "", 0))
        Ok(views.html.index(res))
      }catch {
        case e=>e.printStackTrace()
          Ok(views.html.addPoke(fullpokeForm,true))
      }
    }

    val formValidationResult = fullpokeForm.bindFromRequest
    formValidationResult.fold(errorFunction, successFunction)
  }
}
