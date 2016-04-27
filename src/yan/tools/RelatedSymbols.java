package yan.tools;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.navigation.GotoRelatedItem;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yan on 2016/4/26.
 */
public class RelatedSymbols extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        String message = "";
        VirtualFile currentFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        Project project = e.getData(PlatformDataKeys.PROJECT);

        if (isTargetFile(currentFile.getCanonicalPath())){
            List<GotoRelatedItem> items = findRelatedSymbols(currentFile,project);
            if (items.isEmpty()){
                message="Sorry,nothing related found!";
            }else{
                NavigationUtil.getRelatedItemsPopup(items, "Choose Target").showInBestPositionFor(e.getDataContext());
                return;
            }
        }else {
            message = "Hi,this tools is for grails related files of domain,controller and views,\nyour current file looks like not the target file.";
        }

        if (!message.equals("")){
            Messages.showMessageDialog(project, message, "Information", Messages.getInformationIcon());
        }
    }

    private boolean isTargetFile(String file){
        if (file.endsWith("groovy") || file.endsWith("gsp")){
            return true;
        }
        return false;
    }

    private List<GotoRelatedItem> findRelatedSymbols(VirtualFile currentFile,Project project){
        List<GotoRelatedItem> items = new ArrayList<GotoRelatedItem>();
        if (isTargetFile(currentFile.getCanonicalPath())){
            String projectRoot = project.getBasePath();
            List<File> list = GrailsToolsUtil.findRelatedFiles(currentFile.getCanonicalPath(),projectRoot);
            //GrailsToolsUtil.printList(list);
            for (File file : list){
                VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
                PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
                if (psiFile!=null){
                    GotoRelatedItem gotoRelatedItem = new GotoRelatedItem(psiFile);
                    items.add(gotoRelatedItem);
                }
            }
        }

        return items;
    }
}
