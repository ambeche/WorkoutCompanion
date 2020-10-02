package com.example.workoutcompanion.model

object APIcalls {

    val Diet_endPoint ="https://api.spoonacular.com/recipes/complexSearch?apiKey=${MyToken.apiKey}"


    val Nutrition_Recipe = "https://api.spoonacular.com/recipes/findByNutrients?apiKey=${MyToken.apiKey}"



   fun Similar_recipres(id : Int) : String {

        var Similar = "https://api.spoonacular.com/recipes/${id}/similar?apiKey=${MyToken.apiKey}"
       return Similar

    }
    fun Summarize_Recipes(id : Int?) : String {
        var Summarize = "https://api.spoonacular.com/recipes/${id}/summary?apiKey=${MyToken.apiKey}"
        return Summarize
    }

    fun Meal_Generating(time:String,calory:Int,diet:String):String{

        var Generatmeal = "https://api.spoonacular.com/mealplanner/generate"

        Generatmeal += "?apiKey=${MyToken.apiKey}&timeFrame=${time}&targetCalories=${calory}&diet=${diet}"
        return Generatmeal
    }

    fun REcipe_Info_Specific(id:Int?) : String{
        val info_recipe = "https://api.spoonacular.com/recipes/${id}/information?apiKey=${MyToken.apiKey}&includeNutrition=true"
        return info_recipe
    }

    fun REcipe_Info_(id:Int?) : String{
        val info_recipe = "https://api.spoonacular.com/recipes/${id}/nutritionWidget.json?apiKey=${MyToken.apiKey}"
        return info_recipe
    }

    fun SearchByCal(name:String?,min:String?,max:String?,type:String?):String {
        val searchMeal = Diet_endPoint + "&minCalories=${min}&maxCalories=${max}&query=${name}&diet=${type}&number=2"
        return searchMeal
    }
}

object MyToken {
    val apiKey = "6632e51f9fd646e7b179ea2da266a4af"
}