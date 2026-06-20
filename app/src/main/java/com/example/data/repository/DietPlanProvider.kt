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
            carbsTarget = 165f,
            fatTarget = 44f,
            meals = listOf(
                DietMeal(
                    mealType = "Breakfast",
                    foodName = "Phở Gà (Skinless Chicken Breast)",
                    portion = "1 classic bowl (500g): skinless white chicken breast meat, light cardamom chicken bone broth, raw bean sprouts, fresh mint.",
                    calories = 390,
                    protein = 30f,
                    carbs = 50f,
                    fat = 8f,
                    note = "Selecting skinless white breast meat minimizes fat intake. Top generously with fresh bean sprouts and herbs to increase meal volume."
                ),
                DietMeal(
                    mealType = "Lunch",
                    foodName = "Gỏi Cuốn Tôm Thịt (Fresh Summer Rolls)",
                    portion = "4 fresh rice paper rolls (240g): boiled plump shrimp, lean skinless pork shoulder slices, crisp lettuce, cucumbers, chives, mint.",
                    calories = 320,
                    protein = 24f,
                    carbs = 42f,
                    fat = 6f,
                    note = "Enjoy with custom light dipping fish sauce rather than sweet peanut dipping sauce to keep fat levels and calories lower."
                ),
                DietMeal(
                    mealType = "Dinner",
                    foodName = "Canh Chua Cá Lóc (Snakehead Fish Sour Soup) with Jasmine Rice",
                    portion = "1 bowl sweet-sour soup with 150g lean snakehead fish fillet, tamarind stock, tomatoes, and dwarf bananas + 1.5 bowls (200g) of cooked Jasmine rice.",
                    calories = 425,
                    protein = 32f,
                    carbs = 52f,
                    fat = 10f,
                    note = "Local snakehead fish is exceptionally lean and digestible. The fibrous vegetables in the sour broth encourage early meal satiation."
                ),
                DietMeal(
                    mealType = "Snack",
                    foodName = "Greek Yogurt 0% with Fresh Strawberries",
                    portion = "150g plain fat-free Greek yogurt + 80g fresh sliced strawberries.",
                    calories = 145,
                    protein = 18f,
                    carbs = 14f,
                    fat = 2f,
                    note = "A low-calorie, high-protein choice designed to naturally satisfy appetite between main meals."
                ),
                DietMeal(
                    mealType = "Snack",
                    foodName = "Rau Muống Xào Tỏi (Garlic Morning Glory)",
                    portion = "1 large plate (250g) stir-fried hollow spinach using 1 tsp vegetable oil and plenty of crushed garlic.",
                    calories = 220,
                    protein = 6f,
                    carbs = 7f,
                    fat = 18f,
                    note = "Morning glory is rich in insoluble dietary fiber and micronutrients, supporting highly regular digestability and glycemic stability."
                )
            ),
            generalAdvice = "Maintain consistent hydration by drinking at least 2.5 liters of water daily. Focus on adding high-volume leafy vegetables to keep meals satisfying."
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
                    portion = "1 master plate (500g): steamed broken rice, charcoal-grilled pork chop, two fresh eggs (sunny-side up), scallion glaze.",
                    calories = 785,
                    protein = 45f,
                    carbs = 100f,
                    fat = 25f,
                    note = "Provides complete proteins, essential minerals, and dense rice carbohydrates to kickstart muscle glycogen replenishment."
                ),
                DietMeal(
                    mealType = "Lunch",
                    foodName = "Bún Chả Hà Nội (Pork Patty Noodles)",
                    portion = "1 heavy bowl (550g): rice vermicelli noodles, dipping broth, grilled lean pork patties & slices, plenty of herbs.",
                    calories = 630,
                    protein = 32f,
                    carbs = 90f,
                    fat = 16f,
                    note = "Rice starches are easily digested and serve as excellent muscle energy reinforcement post-exercise."
                ),
                DietMeal(
                    mealType = "Dinner",
                    foodName = "Thịt Kho Tàu (Caramelized Pork & Eggs) with Steamed Jasmine Rice",
                    portion = "180g caramelized pork breast & loin simmered with 2 hard-boiled eggs in fresh coconut water, served with 2 cups of steamed Jasmine rice.",
                    calories = 800,
                    protein = 38f,
                    carbs = 80f,
                    fat = 26f,
                    note = "A high-energy recovery meal. Quality fats and proteins support overall muscular recovery and energy availability."
                ),
                DietMeal(
                    mealType = "Snack",
                    foodName = "Double Scoop Whey Protein Shake & Fresh Banana",
                    portion = "2 scoops premium gold standard whey protein mixed in chilled water + 1 large ripe banana (120g).",
                    calories = 285,
                    protein = 45f,
                    carbs = 30f,
                    fat = 3f,
                    note = "Highly convenient source of dietary proteins and easy-burning simple carbs before or after intensive workouts."
                )
            ),
            generalAdvice = "Maintain a consistent high-protein intake through the day. Prioritize quality rest (7-8 hours) to support muscle recovery and training adaptations."
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
                    calories = 490,
                    protein = 34f,
                    carbs = 62f,
                    fat = 12f,
                    note = "Combines dietary iron, amino acids, and complex rice noodles in a comforting, hydrating mineral broth."
                ),
                DietMeal(
                    mealType = "Lunch",
                    foodName = "Bún Thịt Nướng (Grilled Lemongrass Pork Noodles)",
                    portion = "1 bowl (420g): vermicelli rice noodles, grilled lemongrass pork loin, roasted peanuts, lettuce, mint, sweet fish sauce.",
                    calories = 530,
                    protein = 26f,
                    carbs = 72f,
                    fat = 15f,
                    note = "Maintains a balanced carbohydrate and protein feed to support steady physical energy through the afternoon."
                ),
                DietMeal(
                    mealType = "Dinner",
                    foodName = "Pan-Seared Salmon Fillet with Steamed Rice and Broccoli",
                    portion = "150g baked center-cut Atlantic salmon, 150g steamed jasmine rice, 100g vibrant steamed broccoli florets.",
                    calories = 630,
                    protein = 40f,
                    carbs = 52f,
                    fat = 16f,
                    note = "Salmon provides excellent essential unsaturated fatty acids (Omega-3s), supporting robust cardiovascular health."
                ),
                DietMeal(
                    mealType = "Snack",
                    foodName = "Iced Milk Coffee & Greek Yogurt with Almonds & Apple",
                    portion = "1 glass (180ml) traditional iced milk coffee paired with 150g clean strained Greek yogurt, 15g almonds, and a crisp red apple.",
                    calories = 350,
                    protein = 30f,
                    carbs = 34f,
                    fat = 12f,
                    note = "Rich in dietary calcium and minerals, containing simple coffee triggers paired with nutritious whole-food fats."
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
