package com.sjm.filemap.screens.filelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sjm.filemap.ui.theme.DialogBackground
import com.sjm.filemap.ui.theme.FileMapTheme
import com.sjm.filemap.ui.theme.Gray
import com.sjm.filemap.ui.theme.MainGreen
import com.sjm.filemap.ui.theme.SecondaryBlue
import com.sjm.filemap.ui.theme.White

@Composable
fun InputDialog(
    prompt: String,
    defaultText: String = "",
    acceptButtonText: String = "ACEPTAR",
    onAccept: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var field by remember { mutableStateOf(defaultText) }
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(30.dp), backgroundColor = DialogBackground) {
            Column(
                Modifier
                    .padding(horizontal = 28.dp, vertical = 16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = prompt,
                    fontSize = 24.sp,
                    color = White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = field,
                    onValueChange = { field = it },
                    shape = RoundedCornerShape(18.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = White, unfocusedBorderColor = Gray
                    )
                )

                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Button(
                        onClick = { onDismiss() },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = SecondaryBlue, contentColor = White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("CANCELAR")
                    }
                    Button(
                        shape = RoundedCornerShape(20.dp),
                        onClick = { onAccept(field) },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MainGreen, contentColor = White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(acceptButtonText)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDialog() {
    FileMapTheme {

    }
}