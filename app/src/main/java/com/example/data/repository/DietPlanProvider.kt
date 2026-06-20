package com.example.data.repository

data class DietMeal(
    val mealType: String, // "Breakfast", "Lunch", "Dinner", "Snack"
    val foodName: String,
    val portion: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val note: String
)

data class DietPlan(
    val goal: String, // "Weight Loss", "Muscle Gain", "Balanced Maintenance"
    val title: String,
    val subtitle: String,
    val description: String,
    val calorieTarget: Int,
    val proteinTarget: Float,
    val carbsTarget: Float,
    val fatTarget: Float,
    val meals: List<DietMeal>,
    val generalAdvice: String
)

object DietPlanProvider {

    val PLANS = listOf(
        DietPlan(
            goal = "Weight Loss",
            title = "Calorie Deficit Shred & Lean Plan",
            subtitle = "Vietnamese-inspired weight loss plan",
            description = "A meticulously tailored fat-loss strategy utilizing low-fat, highly satisfying Vietnamese cuisine staples to satisfy hunger while meeting caloric constraints.",
            calorieTarget = 1500,
            proteinTarget = 110f,
            carbsTarget = 150f,
            fatTarget = 40f,
            meals = listOf(
                DietMeal(
                    mealType = "Breakfast",
                    foodName = "Phở Gà (Skinless Chicken Breast)",
                    portion = "1 medium bowl (500g): skinless white chicken breast meat, light cardamom broth, raw bean sprouts, fresh mint.",
                    calories = 340,
                    protein = 24.5f,
                    carbs = 48f,
                    fat = 5f,
                    note = "Ditch chicken skin completely. Top generously with raw bean sprouts and lime to increase fiber volume."
                ),
                DietMeal(
                    mealType = "Lunch",
                    foodName = "Gỏi Cuốn Tôm Thịt (Fresh Summer Rolls)",
                    portion = "3 rolls (180g): steamed prawns, very lean pork slices, fresh lettuce, cucumbers, chives, mint wrapped in rice paper.",
                    calories = 225,
                    protein = 15f,
                    carbs = 33f,
                    fat = 3.5f,
                    note = "Enjoy with custom light dipping fish sauce (not peanut butter sauce) to keep fat levels extremely minimal."
                ),
                DietMeal(
                    mealType = "Dinner",
                    foodName = "Canh Chua Cá Lóc (Snakehead Fish Sour Soup) with Jasmine Rice",
                    portion = "1 large bowl soup with 200g snakehead fish fillet, tamarind stock, pineapples, okra + 100g steamed Jasmine rice.",
                    calories = 410,
                    protein = 31f,
                    carbs = 56f,
                    fat = 5f,
                    note = "Catfish or snakehead fish is exceptionally lean. Fiber in taro stems and tomatoes fills the stomach easily."
                ),
                DietMeal(
                    mealType = "Snack",
                    foodName = "Greek Yogurt 0% with Fresh Strawberries",
                    portion = "150g plain greek yogurt + 50g fresh sliced strawberries.",
                    calories = 110,
                    protein = 15f,
                    carbs = 10f,
                    fat = 0.5f,
                    note = "An absolute low-calorie protein powerhouse to curb mid-afternoon sweet cravings."
                ),
                DietMeal(
                    mealType = "Snack",
                    foodName = "Rau Muống Xào Tỏi (Garlic Morning Glory)",
                    portion = "1 large plate (200g) stir-fried hollow spinach using only 1/2 tsp olive oil and lots of fresh garlic.",
                    calories = 95,
                    protein = 4.5f,
                    carbs = 8f,
                    fat = 5f,
                    note = "Extremely high in dietary insoluble fibers which suppress metabolic insulin spikes."
                )
            ),
            generalAdvice = "Ensure you scale down liquid calorie intake. Drink at least 2.5 liters of clean water daily, and prioritize leafy herbs to increase physical meal density."
        ),
        DietPlan(
            goal = "Muscle Gain",
            title = "Anabolic Hypertrophy Bulking Plan",
            subtitle = "Vietnamese-inspired muscle builder plan",
            description = "A calorie-dense, high-protein anabolic menu leveraging rich Vietnamese meats and clean carbohydrates to fuel intense weight training and support recovery.",
            calorieTarget = 2500,
            proteinTarget = 160f,
            carbsTarget = 300f,
            fatTarget = 70f,
            meals = listOf(
                DietMeal(
                    mealType = "Breakfast",
                    foodName = "Cơm Tấm Sườn Nướng & Trứng Ôp La",
                    portion = "1 large plate (500g): steamed broken rice, charcoal-grilled pork chop, two fried eggs (sunnyside up), green onion oil glaze.",
                    calories = 780,
                    protein = 42f,
                    carbs = 98f,
                    fat = 24f,
                    note = "Rich source of zinc, quality fats, and dense carbohydrates to kickstart muscle cell energy saturation."
                ),
                DietMeal(
                    mealType = "Lunch",
                    foodName = "Bún Chả Hà Nội (Pork Patty Noodles)",
                    portion = "1 massive bowl (550g): rice vermicelli noodles, sweet dipping broth, charcoal-grilled pork meatballs and slides.",
                    calories = 620,
                    protein = 28f,
                    carbs = 85f,
                    fat = 18f,
                    note = "Highly digestible standard rice starches replenish muscle glycogen stores quickly after hard workouts."
                ),
                DietMeal(
                    mealType = "Dinner",
                    foodName = "Thịt Kho Tàu (Caramelized Pork & Eggs) with Steamed Jasmine Rice",
                    portion = "200g caramelized pork belly and 2 hard-boiled eggs braised in sweet coconut juice + 200g double-steamed white rice.",
                    calories = 810,
                    protein = 38f,
                    carbs = 76f,
                    fat = 38f,
                    note = "High saturated fats coupled with whole egg lecithin boost testosterone synthesis, fueling muscular strength."
                ),
                DietMeal(
                    mealType = "Snack",
                    foodName = "Double Scoop Whey Protein Shake & Fresh Banana",
                    portion = "2 scoops premium gold standard whey protein mixed in water + 1 large ripe banana (120g).",
                    calories = 340,
                    protein = 49f,
                    carbs = 30f,
                    fat = 3f,
                    note = "Drink within 45 minutes post-workout for maximum physical recovery and amino-acid muscle uptake."
                )
            ),
            generalAdvice = "Maintain a steady positive nitrogen status. Never miss meals, drink over 3 liters of water to manage high kidney filtration loads, and sleep 8+ hours."
        ),
        DietPlan(
            goal = "Balanced Maintenance",
            title = "Steady Metabolic Homeostasis Plan",
            subtitle = "Vietnamese-inspired balanced plan",
            description = "A versatile, nutritional-dense lifestyle program balancing micronutrient density, fibrous greens, clean starches, and rich minerals to support clean longevity.",
            calorieTarget = 2000,
            proteinTarget = 130f,
            carbsTarget = 220f,
            fatTarget = 55f,
            meals = listOf(
                DietMeal(
                    mealType = "Breakfast",
                    foodName = "Phở Bò (Lean Flank Beef Noodle Bowl)",
                    portion = "1 master bowl (680g): flat rice noodles, 120g premium lean beef flank cuts, slow simmered anise anise broth, herbs.",
                    calories = 450,
                    protein = 32f,
                    carbs = 58f,
                    fat = 12f,
                    note = "Excellent blend of muscle glycogen starches, lean red beef iron, and highly restorative collagen bone broth."
                ),
                DietMeal(
                    mealType = "Lunch",
                    foodName = "Bún Thịt Nướng (Grilled Lemongrass Pork Noodles)",
                    portion = "1 bowl (420g): vermicelli rice noodles, grilled lemongrass pork loin, roasted peanuts, lettuce, mint, sweet fish sauce.",
                    calories = 510,
                    protein = 24f,
                    carbs = 70f,
                    fat = 14f,
                    note = "Keeps dynamic carbohydrate supply steady during daytime activities without creating sluggish bloating."
                ),
                DietMeal(
                    mealType = "Dinner",
                    foodName = "Pan-Seared Salmon Fillet with Steamed Rice and Broccoli",
                    portion = "150g baked center-cut Atlantic salmon, 150g steamed jasmine rice, 100g vibrant steamed broccoli florets.",
                    calories = 610,
                    protein = 36f,
                    carbs = 48f,
                    fat = 18f,
                    note = "High cellular omega-3 fats lower cellular blood pressure and neutralize persistent daily mental exhaustion."
                ),
                DietMeal(
                    mealType = "Snack",
                    foodName = "Cà Phê Sữa Đá (Iced Robusta Milk Coffee) & Apple",
                    portion = "1 glass (180ml) phin drip Robusta with 1.5 tbsp condensed milk + 1 medium crisp red apple (130g).",
                    calories = 230,
                    protein = 3.5f,
                    carbs = 38f,
                    fat = 5.5f,
                    note = "Boosts cognitive focus during midday, pairing Robusta antioxidants with fiber rich apple pectins."
                )
            ),
            generalAdvice = "Excellent health profile. Emphasize mindful chewing, minimize carbonated soft-drinks, and maintain 10k daily physical steps."
        )
    )

    fun getPlanForGoal(goal: String): DietPlan {
        return PLANS.firstOrNull { it.goal.equals(goal, ignoreCase = true) }
            ?: PLANS[2] // Default to Balanced
    }
}
