package com.example.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.PaymentRecord
import com.example.domain.model.PaymentStatus
import com.example.domain.repository.PaymentRepository
import com.example.presentation.components.EmptyState
import com.example.presentation.components.LankaJobsTopBar
import com.example.ui.theme.LankaJobsTheme

@Composable
fun MyPaymentsScreen(
    userId: String,
    paymentRepository: PaymentRepository,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userPayments by paymentRepository.getUserPayments(userId).collectAsState(initial = emptyList())
    val tokens = LankaJobsTheme.tokens

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            LankaJobsTopBar(
                title = "My Payments",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(tokens.background)
        ) {
            if (userPayments.isEmpty()) {
                EmptyState(
                    title = "No Payment Submissions Found",
                    subtitle = "When you submit bank deposit slips for advertisements, their verification status will appear here.",
                    icon = Icons.Default.Receipt,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("my_payments_list"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(userPayments, key = { it.id }) { payment ->
                        PaymentRecordCard(payment = payment)
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentRecordCard(payment: PaymentRecord) {
    val tokens = LankaJobsTheme.tokens
    val statusColor = when (payment.status) {
        PaymentStatus.VERIFIED -> tokens.success
        PaymentStatus.SUBMITTED -> tokens.warning
        PaymentStatus.REJECTED -> tokens.error
        PaymentStatus.PENDING -> tokens.secondary
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = tokens.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("payment_card_${payment.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = payment.status.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "LKR %,d".format(payment.amountLkr),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = tokens.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = payment.packageName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = tokens.textPrimary
            )

            Text(
                text = "Method: ${payment.paymentMethod}",
                style = MaterialTheme.typography.bodySmall,
                color = tokens.textSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Ref: ${payment.paymentReference}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = tokens.textPrimary
                )
                Text(
                    text = "Date: ${payment.paymentDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textMuted
                )
            }

            // Receipt preview thumbnail if available
            if (!payment.receiptUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(tokens.surfaceElevated, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    AsyncImage(
                        model = payment.receiptUrl,
                        contentDescription = "Slip thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(1.dp, tokens.border, RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Bank Slip Attached", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = tokens.textPrimary)
                        Text("Submitted for admin verification", style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary, fontSize = 11.sp)
                    }
                }
            }

            // If rejected, show admin rejection reason
            if (payment.status == PaymentStatus.REJECTED && !payment.rejectionReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = tokens.error.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, tokens.error.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = tokens.error, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Admin Verification Note:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = tokens.error
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = payment.rejectionReason ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textPrimary
                        )
                    }
                }
            }
        }
    }
}
