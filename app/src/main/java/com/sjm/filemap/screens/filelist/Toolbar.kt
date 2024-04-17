package com.sjm.filemap.screens.filelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sjm.filemap.ui.theme.ActionToolbarBg
import com.sjm.filemap.ui.theme.ToolbarButtonColor
import com.sjm.filemap.ui.theme.White

@Composable
fun ActionToolbar(isFileSelected: Boolean, onNewFolder: () -> Unit, onBack: () -> Unit) {
    Surface(
        color = ActionToolbarBg,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp, 10.dp),
        shape = RoundedCornerShape(30.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(14.dp, 10.dp),
        ) {
            ToolbarButton({ onBack() }) {
                Text("Atrás")
            }
            if (isFileSelected) {
                ToolbarButton({}) {
                    Text("Pegar")
                }
                ToolbarButton({}) {
                    Text("Cortar")
                }
                ToolbarButton({}) {
                    Text("Copiar")
                }
            } else {
                Spacer(modifier = Modifier.weight(1F))
                ToolbarButton({ onNewFolder() }) {
                    Text("Nuevo")
                }
            }
        }
    }
}

@Composable
fun ToolbarButton(
    onClick: () -> Unit, content: @Composable RowScope.() -> Unit,
) = Button(
    onClick,
    shape = RoundedCornerShape(20.dp),
    content = content,
    colors = ButtonDefaults.buttonColors(
        backgroundColor = ToolbarButtonColor, contentColor = White
    ),
)
