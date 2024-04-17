package com.sjm.filemap.screens.filelist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sjm.filemap.ui.theme.ActionPanelBg
import com.sjm.filemap.ui.theme.White
import com.sjm.filemap.utils.getMimeType

@Composable
fun InfoPanel(vm: SelectionViewModel = viewModel(), listvm: FileListViewModel = viewModel()) {
    AnimatedVisibility(
        visible = vm.isActive(),
        enter = fadeIn(),
        exit = fadeOut(), //FIXME
        modifier = Modifier.zIndex(1F),
    ) {
        if (vm.isActive()) Surface(
            color = ActionPanelBg,
            shape = RoundedCornerShape(30.dp),
            elevation = 5.dp,
            modifier = Modifier.padding(5.dp),
        ) {
            Column {
                SelectionInfo(vm.getSelectionSize())
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    ActionPanelButton(Icons.Outlined.Delete, "Delete", {})

                    if (vm.size() == 1) ActionPanelButton(icon = Icons.Outlined.Edit,
                        description = "Rename",
                        onClick = { vm.showEditDialog = true })
                }
            }
        }
    }

    when {
        vm.showEditDialog -> InputDialog(prompt = "Renombrar", onAccept = { name ->
            vm.rename(name)
            listvm.updateFiles()
        }, onDismiss = { vm.showEditDialog = false }, defaultText = vm.getFirst().name)
    }
}

@Composable
fun SelectionInfo(selectionSize: Long, vm: SelectionViewModel = viewModel()) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .height(90.dp),
    ) {
        if (vm.size() > 1) {
            Text(
                text = "${vm.size()} archivos",
                color = White,
                fontSize = 26.sp,
            )
            Text("Tamaño: ${getAppropriateSize(selectionSize)}", color = White)
        } else {
            val file = vm.getFirst()
            Text(
                text = file.name,
                color = White,
                fontSize = 26.sp,
            )
            Text("Tamaño: ${getAppropriateSize(selectionSize)}", color = White)
            if (file.isDirectory) Text(
                "Numero de archivos: ${file.listFiles()?.size ?: 0}", color = White
            )
            else Text("Tipo: ${file.getMimeType()}", color = White)
        }
    }
}

@Composable
fun ActionPanelButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    Surface(shape = RoundedCornerShape(12.dp), modifier = Modifier.padding(5.dp)) {
        IconButton({ onClick() }, Modifier.size(40.dp)) {
            Icon(
                icon, description, Modifier.size(30.dp)
            )
        }
    }
}
