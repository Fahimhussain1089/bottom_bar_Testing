package com.physicswallah.testingproject.Screen


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
//import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FavoriteBorder
//import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
//import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlin.math.sqrt

private val NavBarHeight = 100.dp
private val NavBarShape = RoundedCornerShape(
    topStart = 32.dp,
    topEnd = 32.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

private val MenuChipHeight = 96.dp
private val MenuChipWidth = 96.dp

private enum class MenuState { Closed, Peek, Open }

private data class MenuEntry(
    val id: Int,
    val label: String,
    val icon: ImageVector
)

/**
 * Bottom bar with a peekable edit sheet and draggable menu chips.
 *
 * Chips can be dragged, deleted by dropping into a delete area and smoothly return
 * to their slot when dropped elsewhere. A cart icon shows a bounce animation and
 * a badge when an item is deleted.
 */
@Composable
fun MenuBottomBar(
    modifier: Modifier = Modifier,
) {
    var menuState by remember { mutableStateOf(MenuState.Closed) }
    var cartHasBadge by remember { mutableStateOf(false) }
    var cartBounceTrigger by remember { mutableStateOf(0) }

    val menuEntries = remember {
        mutableStateListOf(
            MenuEntry(0, "Orders", Icons.Default.LocationOn),
            MenuEntry(1, "Deals", Icons.Default.MailOutline),
            MenuEntry(2, "Browse", Icons.Default.FavoriteBorder),
            MenuEntry(3, "Stores", Icons.Default.Place),
            MenuEntry(4, "Saved", Icons.Default.AccountBox)
        )
    }

    var draggingEntryId by remember { mutableStateOf<Int?>(null) }
    var dragBaseOffset by remember { mutableStateOf(Offset.Zero) }
    var dragDelta by remember { mutableStateOf(Offset.Zero) }

    var deleteZoneBounds by remember { mutableStateOf<Rect?>(null) }

    var isReturning by remember { mutableStateOf(false) }
    var returnStart by remember { mutableStateOf(Offset.Zero) }
    var returnTarget by remember { mutableStateOf(Offset.Zero) }

    val density = LocalDensity.current
    val chipWidthPx = with(density) { MenuChipWidth.toPx() }
    val chipHeightPx = with(density) { MenuChipHeight.toPx() }

    val dragCollapseThresholdPx = with(density) { 12.dp.toPx() }
    val dragDistancePx = sqrt(dragDelta.x * dragDelta.x + dragDelta.y * dragDelta.y)
    val dragHasPassedThreshold = dragDistancePx > dragCollapseThresholdPx

    val returnProgress by animateFloatAsState(
        targetValue = if (isReturning) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "returnProgress"
    )

    val transition = updateTransition(targetState = menuState, label = "menu")

    val barOffsetX: Dp by transition.animateDp(
        transitionSpec = { tween(durationMillis = 550) },
        label = "barOffsetX"
    ) { state ->
        when (state) {
            MenuState.Closed -> 0.dp
            MenuState.Peek,
            MenuState.Open -> (-210).dp
        }
    }

    val sheetOffsetY: Dp by transition.animateDp(
        transitionSpec = { tween(durationMillis = 500) },
        label = "sheetOffsetY"
    ) { state ->
        when (state) {
            MenuState.Closed,
            MenuState.Peek -> 360.dp
            MenuState.Open -> 0.dp
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFC107))
    ) {
        DemoPageContent(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = menuState == MenuState.Open) {
                    menuState = MenuState.Closed
                }
        )

        MenuEditSheet(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset { IntOffset(0, sheetOffsetY.roundToPx()) },
            entries = menuEntries,
            draggingEntryId = draggingEntryId,
            dragHasPassedThreshold = dragHasPassedThreshold,
            isReturning = isReturning,
            onDoneClick = { menuState = MenuState.Closed },
            onStartDrag = { entry, startOffset ->
                isReturning = false
                returnStart = Offset.Zero
                returnTarget = Offset.Zero
                draggingEntryId = entry.id
                dragBaseOffset = startOffset
                dragDelta = Offset.Zero
            },
            onDrag = { delta ->
                dragDelta += delta
            },
            onEndDrag = { entry ->
                val finalOffset = dragBaseOffset + dragDelta

                val chipRect = Rect(
                    finalOffset,
                    finalOffset + Offset(chipWidthPx, chipHeightPx)
                )

                val droppedInDeleteZone = deleteZoneBounds?.overlaps(chipRect) == true

                if (droppedInDeleteZone) {
                    menuEntries.removeAll { it.id == entry.id }
                    cartHasBadge = true
                    cartBounceTrigger++
                    draggingEntryId = null
                    dragBaseOffset = Offset.Zero
                    dragDelta = Offset.Zero
                    isReturning = false
                } else {
                    isReturning = true
                    returnStart = finalOffset
                    returnTarget = dragBaseOffset
                }
            },
            onSlotFullyExpanded = {
                isReturning = false
                draggingEntryId = null
                dragBaseOffset = Offset.Zero
                dragDelta = Offset.Zero
            }
        )

        MenuPeekLayer(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            onOpenClick = { menuState = MenuState.Open }
        )

        DemoBottomNavBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset { IntOffset(barOffsetX.roundToPx(), 0) },
            onProfileClick = {
                menuState = when (menuState) {
                    MenuState.Closed -> MenuState.Peek
                    MenuState.Peek -> MenuState.Closed
                    MenuState.Open -> MenuState.Open
                }
            },
            cartHasBadge = cartHasBadge,
            cartBounceTrigger = cartBounceTrigger
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 24.dp)
                .size(96.dp)
                .onGloballyPositioned { coords ->
                    val topLeft = coords.localToRoot(Offset.Zero)
                    val size = coords.size
                    deleteZoneBounds = Rect(
                        topLeft,
                        topLeft + Offset(size.width.toFloat(), size.height.toFloat())
                    )
                }
        )

        val overlayEntry = menuEntries.firstOrNull { it.id == draggingEntryId }
        if (overlayEntry != null) {
            val rawOffset = if (isReturning) {
                Offset(
                    x = returnStart.x + (returnTarget.x - returnStart.x) * returnProgress,
                    y = returnStart.y + (returnTarget.y - returnStart.y) * returnProgress
                )
            } else {
                dragBaseOffset + dragDelta
            }

            val isDraggingOverlay = draggingEntryId != null && !isReturning
            val overlayScale by animateFloatAsState(
                targetValue = if (isDraggingOverlay) 0.9f else 1f,
                animationSpec = tween(durationMillis = 120),
                label = "overlayScale"
            )

            MenuChip(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset {
                        IntOffset(
                            rawOffset.x.roundToInt(),
                            rawOffset.y.roundToInt()
                        )
                    }
                    .width(MenuChipWidth)
                    .height(MenuChipHeight),
                label = overlayEntry.label,
                icon = overlayEntry.icon,
                scale = overlayScale
            )
        }
    }
}

/**
 * Bottom navigation bar containing home, search, cart and profile.
 *
 * The cart icon can bounce and display a badge when items are deleted
 * from the menu.
 */
@Composable
private fun DemoBottomNavBar(
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit,
    cartHasBadge: Boolean,
    cartBounceTrigger: Int,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(NavBarHeight),
        shape = NavBarShape,
        color = Color(0xFFFFF3D0),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 60.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(Icons.Default.Home, modifier = Modifier.size(30.dp), contentDescription = null)
            Icon(Icons.Default.Search, modifier = Modifier.size(30.dp), contentDescription = null)

            CartIconWithBadge(
                hasBadge = cartHasBadge,
                bounceTrigger = cartBounceTrigger
            )

            Icon(
                Icons.Default.Person,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .clickable { onProfileClick() }
            )
        }
    }
}

/**
 * Peek layer revealed when the bottom bar slides left.
 *
 * Shows secondary actions and a button to fully open the menu sheet.
 */
@Composable
private fun MenuPeekLayer(
    modifier: Modifier = Modifier,
    onOpenClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(NavBarHeight)
            .width(260.dp),
        shape = NavBarShape,
        color = Color(0xFF050509),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { }) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color(0xFFFFF3D0)
                )
            }

            IconButton(onClick = onOpenClick) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    modifier = Modifier.size(30.dp),
                    contentDescription = "Open menu sheet",
                    tint = Color(0xFFFFF3D0)
                )
            }
        }
    }
}

/**
 * Bottom sheet that shows the editable row of menu chips.
 *
 * Supports long-press drag, slot collapsing during drag, and a callback
 * when the slot expansion animation finishes after a return.
 */
@Composable
private fun MenuEditSheet(
    modifier: Modifier = Modifier,
    entries: SnapshotStateList<MenuEntry>,
    draggingEntryId: Int?,
    isReturning: Boolean,
    dragHasPassedThreshold: Boolean,
    onDoneClick: () -> Unit,
    onStartDrag: (MenuEntry, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onEndDrag: (MenuEntry) -> Unit,
    onSlotFullyExpanded: () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(360.dp),
        color = Color(0xFF050509),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your Menu",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Drag and drop options",
                        color = Color(0xFFFFF3D0),
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFFC107), RoundedCornerShape(20.dp))
                        .clickable { onDoneClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close menu",
                        tint = Color.Black
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                itemsIndexed(entries, key = { _, item -> item.id }) { index, entry ->
                    val isBeingDragged = draggingEntryId == entry.id
                    val collapseSlot = isBeingDragged && dragHasPassedThreshold && !isReturning
                    val isLast = index == entries.lastIndex
                    val trailingSpacing = if (collapseSlot) 0.dp else if (isLast) 0.dp else 16.dp

                    DraggableMenuChip(
                        entry = entry,
                        isBeingDragged = isBeingDragged,
                        isReturning = isReturning && isBeingDragged,
                        collapseSlot = collapseSlot,
                        trailingSpacing = trailingSpacing,
                        onStartDrag = { startOffset -> onStartDrag(entry, startOffset) },
                        onDrag = onDrag,
                        onEndDrag = { onEndDrag(entry) },
                        onSlotFullyExpanded = onSlotFullyExpanded
                    )
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

/**
 * Single draggable chip in the edit sheet row.
 *
 * Handles long-press detection, slot width animation and notifies
 * when the slot has fully expanded after a return animation.
 */
@Composable
private fun DraggableMenuChip(
    entry: MenuEntry,
    isBeingDragged: Boolean,
    isReturning: Boolean,
    collapseSlot: Boolean,
    trailingSpacing: Dp,
    onStartDrag: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onEndDrag: () -> Unit,
    onSlotFullyExpanded: () -> Unit,
) {
    var chipTopLeftInRoot by remember { mutableStateOf(Offset.Zero) }

    val targetWidth = if (collapseSlot) 0.dp else MenuChipWidth

    val width by animateDpAsState(
        targetValue = targetWidth,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "chipWidth"
    )

    LaunchedEffect(width, isBeingDragged, isReturning, collapseSlot) {
        if (isBeingDragged && isReturning && !collapseSlot && width >= MenuChipWidth) {
            onSlotFullyExpanded()
        }
    }

    Box(
        modifier = Modifier
            .padding(end = trailingSpacing)
            .onGloballyPositioned { coords ->
                chipTopLeftInRoot = coords.localToRoot(Offset.Zero)
            }
            .pointerInput(entry.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        onStartDrag(chipTopLeftInRoot)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    },
                    onDragEnd = { onEndDrag() },
                    onDragCancel = { onEndDrag() }
                )
            }
            .width(width)
            .height(MenuChipHeight)
    ) {
        val showChip = !isBeingDragged

        if (showChip) {
            MenuChip(
                modifier = Modifier.fillMaxSize(),
                label = entry.label,
                icon = entry.icon,
                scale = 1f
            )
        }
    }
}

/**
 * Visual representation of a menu chip with dashed border, icon and label.
 *
 * The chip supports uniform scaling and a fixed outer padding between content
 * and the dashed border.
 */
@Composable
private fun MenuChip(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    scale: Float = 1f,
) {
    val chipBg = Color(0xFFFFF3D0)
    val borderColor = Color(0xFFE2D1A4)
    val outerPadding = 4.dp

    Surface(
        modifier = modifier.graphicsLayer(
            scaleX = scale,
            scaleY = scale
        ),
        shape = RoundedCornerShape(20.dp),
        color = chipBg
    ) {
        Box(
            modifier = Modifier
                .padding(outerPadding)
                .dashedRoundedBorder(
                    color = borderColor,
                    cornerRadius = 16.dp,
                    strokeWidth = 1.5.dp,
                    dashLength = 6.dp,
                    gapLength = 4.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = label,
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

/**
 * Draws a dashed rounded border inset inside the modifier bounds.
 */
private fun Modifier.dashedRoundedBorder(
    color: Color,
    cornerRadius: Dp,
    strokeWidth: Dp,
    dashLength: Dp,
    gapLength: Dp
): Modifier = this.then(
    Modifier.drawBehind {
        val strokeWidthPx = strokeWidth.toPx()
        val dashPx = dashLength.toPx()
        val gapPx = gapLength.toPx()
        val halfStroke = strokeWidthPx / 2f

        val pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(dashPx, gapPx),
            0f
        )

        val rect = Rect(
            left = halfStroke,
            top = halfStroke,
            right = size.width - halfStroke,
            bottom = size.height - halfStroke
        )

        val radiusPx = cornerRadius.toPx()

        drawRoundRect(
            color = color,
            topLeft = rect.topLeft,
            size = rect.size,
            cornerRadius = CornerRadius(radiusPx, radiusPx),
            style = Stroke(width = strokeWidthPx, pathEffect = pathEffect)
        )
    }
)

/**
 * Simple placeholder page content behind the bottom bar and edit sheet.
 */
@Composable
private fun DemoPageContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(top = 48.dp, start = 24.dp, end = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        repeat(6) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFFFD54F)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFFFFC107), RoundedCornerShape(18.dp))
                    )

                    Spacer(Modifier.width(20.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .height(14.dp)
                                .fillMaxWidth(0.6f)
                                .background(Color(0xFFFFC107), RoundedCornerShape(7.dp))
                        )
                        Box(
                            modifier = Modifier
                                .height(14.dp)
                                .fillMaxWidth(0.45f)
                                .background(Color(0xFFFFC107), RoundedCornerShape(7.dp))
                        )
                    }
                }
            }
        }
    }
}

/**
 * Shopping cart icon that can bounce and display a small red badge.
 *
 * [bounceTrigger] is used as a monotonically increasing key to re-run the
 * bounce animation when its value changes.
 */
@Composable
private fun CartIconWithBadge(
    hasBadge: Boolean,
    bounceTrigger: Int
) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(bounceTrigger) {
        if (bounceTrigger == 0) return@LaunchedEffect

        scale.snapTo(1f)
        scale.animateTo(
            1.25f,
            animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing)
        )
        scale.animateTo(
            1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Box(
        modifier = Modifier
            .size(30.dp)
            .graphicsLayer(
                scaleX = scale.value,
                scaleY = scale.value
            )
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        if (hasBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(10.dp)
                    .background(Color(0xFFE53935), CircleShape)
            )
        }
    }
}