package com.example.foodhub

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.foodhub.data.FoodApi
import com.example.foodhub.data.models.FoodItem
import com.example.foodhub.ui.features.auth.AuthScreen
import com.example.foodhub.ui.features.auth.login.SignInScreen
import com.example.foodhub.ui.features.auth.signup.SignUpScreen
import com.example.foodhub.ui.features.cart.CartScreen
import com.example.foodhub.ui.features.food_item_details.FoodDetailsScreen
import com.example.foodhub.ui.features.home.HomeScreen
import com.example.foodhub.ui.features.restaurant_details.RestaurantDetailsScreen
import com.example.foodhub.ui.navigation.AuthScreen
import com.example.foodhub.ui.navigation.Cart
import com.example.foodhub.ui.navigation.FoodDetails
import com.example.foodhub.ui.navigation.Home
import com.example.foodhub.ui.navigation.Login
import com.example.foodhub.ui.navigation.RestaurantDetails
import com.example.foodhub.ui.navigation.SignUp
import com.example.foodhub.ui.navigation.foodItemNavType
import com.example.foodhub.ui.theme.FoodHubtrainingTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Objects
import javax.inject.Inject
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var showSplashScreen = true

    @Inject
    lateinit var foodApi: FoodApi

    @Inject
    lateinit var session: FoodHubSession

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                showSplashScreen
            }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    .5f,
                    0f
                )
                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    .5f,
                    0f
                )
                zoomX.duration = 500
                zoomY.duration = 500
                zoomX.interpolator = OvershootInterpolator()
                zoomY.interpolator = OvershootInterpolator()
                zoomX.doOnEnd {
                    screen.remove()
                }
                zoomY.doOnEnd {
                    screen.remove()
                }
                zoomX.start()
                zoomY.start()
            }
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodHubtrainingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
//                    session.storeToken("")
                    SharedTransitionLayout {
                        NavHost(
                            navController = navController,
                            startDestination = if (session.getToken() != "") Home else AuthScreen,
                            modifier = Modifier.padding(innerPadding),
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(300)
                                ) + fadeIn(animationSpec = tween(300))
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300))
                            },
                            popEnterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(300)
                                ) + fadeIn(animationSpec = tween(300))
                            },
                            popExitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300))
                            }
                        ) {
                            composable<SignUp> { SignUpScreen(navController) }
                            composable<AuthScreen> { AuthScreen(navController) }
                            composable<Login> (
                                enterTransition = {
                                    slideIntoContainer(
                                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                                        animationSpec = tween(300)
                                    ) + fadeIn(animationSpec = tween(300))
                                },
                                exitTransition = {
                                    slideOutOfContainer(
                                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                                        animationSpec = tween(300)
                                    ) + fadeOut(animationSpec = tween(300))
                                },
                                popEnterTransition = {
                                    slideIntoContainer(
                                        towards = AnimatedContentTransitionScope.SlideDirection.Down,
                                        animationSpec = tween(300)
                                    ) + fadeIn(animationSpec = tween(300))
                                },
                                popExitTransition = {
                                    slideOutOfContainer(
                                        towards = AnimatedContentTransitionScope.SlideDirection.Down,
                                        animationSpec = tween(300)
                                    ) + fadeOut(animationSpec = tween(300))
                                }
                            ) {
                                SignInScreen(navController)
                            }
                            composable<Home> {
                                HomeScreen(navController, this)
                            }
                            composable<RestaurantDetails> {
                                val route = it.toRoute<RestaurantDetails>()
                                RestaurantDetailsScreen(
                                    navController,
                                    name = route.restaurantName,
                                    imageUrl = route.restaurantImageUrl,
                                    restaurantID = route.restaurantId,
                                    this
                                )
                            }
                            composable<FoodDetails>(
                                typeMap = mapOf(typeOf<FoodItem>() to foodItemNavType)
                            ) {
                                val route = it.toRoute<FoodDetails>()
                                FoodDetailsScreen(
                                    navController,
                                    foodItem = route.foodItem,
                                    this
                                )
                            }
                            composable<Cart>() {
                                CartScreen(navController)
                            }
                        }
                    }




//                    Box(modifier = Modifier.padding(innerPadding)) {
//                        SignUpScreen()
//                    }
                }
            }
        }

        if(::foodApi.isInitialized) {
            Log.d("MainAct", "FoodApi init")
        }

        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            showSplashScreen = false
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FoodHubtrainingTheme {
        Greeting("Android")
    }
}