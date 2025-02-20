package com.example.foodhub.ui.features.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.foodhub.R
import com.example.foodhub.data.models.Category
import com.example.foodhub.data.models.Restaurant
import com.example.foodhub.ui.navigation.RestaurantDetails
import com.example.foodhub.ui.theme.Orange
import com.example.foodhub.ui.theme.Typography
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest {
            when(it) {
                is HomeViewModel.HomeScreenNavigationEvents.NavigateToDetail -> {
                    navController.navigate(RestaurantDetails(
                        restaurantName = it.name,
                        restaurantId = it.id,
                        restaurantImageUrl = it.imageUrl
                    ))
                }
                else -> {}
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        val uiState = viewModel.uiState.collectAsState()

        when(uiState.value) {
            is HomeViewModel.HomeScreenState.Loading -> { Text(text = "Loading") }
            is HomeViewModel.HomeScreenState.Empty -> { Text(text = "Empty") }
            is HomeViewModel.HomeScreenState.Success -> {
                val categories = viewModel.categories

                CategoriesList(categories = categories, onCategorySelected = {
                    navController.navigate("category/${it.id}")
                })

                RestaurantsList(
                    restaurants = viewModel.restaurants,
                    animatedVisibilityScope,
                    onRestaurantSelected = {
                        viewModel.onRestaurantSelected(it)
                    }
                )
            }
        }

    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantsList(
    restaurants: List<Restaurant>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onRestaurantSelected: (Restaurant) -> Unit
) {
    Column {
        Row {
            Text(
                text = "Popular Restaurants",
                style = Typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { /*TODO*/ }) {
                Text(text = "View All", style = Typography.bodySmall)
            }
        }
    }
    LazyRow {
        items(restaurants) {
            RestaurantItem(it, animatedVisibilityScope, onRestaurantSelected)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantItem(
    restaurant: Restaurant,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onRestaurantSelected: (Restaurant) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .width(250.dp)
            .height(229.dp)
            .shadow(16.dp, shape = RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onRestaurantSelected(restaurant) }
            .clip(RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = restaurant.image_url,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .sharedElement(
                        state = rememberSharedContentState(key = "image/${restaurant.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    ),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier
                .background(Color.White)
                .padding(12.dp)
                .clickable { onRestaurantSelected(restaurant) }) {
                Text(
                    text = restaurant.name,
                    style = Typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.sharedElement(
                        state = rememberSharedContentState(key = "title/${restaurant.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                )
                Row() {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_delivery),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .padding(end = 8.dp)
                                .size(12.dp)
                        )
                        Text(
                            text = "Free Delivery",
                            style = Typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.timer),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .padding(end = 8.dp)
                                .size(12.dp)
                        )
                        Text(
                            text = "Free Delivery",
                            style = Typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .align(TopStart)
                .padding(8.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center

        ) {
            Text(
                text = "4.5", style = Typography.titleSmall,

                modifier = Modifier.padding(4.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Image(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Yellow)
            )
            Text(
                text = "(25)", style = Typography.bodySmall, color = Color.Gray
            )
        }
    }
}

@Composable
fun CategoriesList(
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit
) {
    LazyRow {
        items(categories) {
            CategoryItem(category = it, onCategorySelected = onCategorySelected)
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    onCategorySelected: (Category) -> Unit
) {
    Column(modifier = Modifier
        .padding(8.dp)
        .height(90.dp)
        .width(60.dp)
        .clickable { onCategorySelected(category) }
        .shadow(
            16.dp,
            shape = RoundedCornerShape(45.dp),
            ambientColor = Color.Gray.copy(alpha = 0.8f),
            spotColor = Color.Gray.copy(alpha = 0.8f)
        )
        .background(Color.White)
        .clip(RoundedCornerShape(45.dp))
        .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = category.image_url,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .shadow(
                    8.dp,
                    shape = CircleShape,
                    ambientColor = Orange,
                    spotColor = Orange
                )
                .clip(CircleShape),
            contentScale = ContentScale.Inside
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = category.name,
            style = TextStyle(fontSize = 10.sp),
            textAlign = TextAlign.Center
        )
    }
}

