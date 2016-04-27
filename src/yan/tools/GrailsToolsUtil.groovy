package yan.tools

import groovy.util.logging.Log

/**
 * Created by yan on 2016/4/26.
 */
@Log
class GrailsToolsUtil {

    static List<File> findRelatedFiles(String fileName,String projectPath){
        List<File> list = new ArrayList<>();
        File currentFile = new File(fileName),root = new File(projectPath)
        String key = ''

        if (fileName.endsWith('gsp')){
            key = currentFile.parentFile.name
        }
        if (fileName.endsWith('groovy')){
            if (fileName.contains('Controller')){
                key = currentFile.name.substring(0,currentFile.name.lastIndexOf("Controller"))
            }else {
                key = currentFile.name.substring(0,currentFile.name.lastIndexOf('.'))
            }
        }
        key = key.toLowerCase()

        File domainDir = new File(root,'grails-app/domain')
        File controllerDir = new File(root,'grails-app/controllers')
        File viewsDir = new File(root,'grails-app/views/'+key)

        domainDir.eachFileRecurse {
            if (it.name.toLowerCase().equals(key+'.groovy')){
                list << it
            }
        }
        controllerDir.eachFileRecurse {
            if (it.name.toLowerCase().contains(key+'controller.groovy')){
                list << it
            }
        }
        if (viewsDir.exists()){
            viewsDir.eachFileRecurse {
                list << it
            }
        }

        list = list.findAll{
            it.canonicalPath != currentFile.canonicalPath
        }
        return list
    }

    static void printList(List list){
        println("list size $list.size")
        list.each{
            println(it)
        }
    }

    public static void main(String[] args) {
        printList(findRelatedFiles('D:\\temp\\Hello315\\grails-app\\controllers\\yan\\BookController.groovy','D:\\temp\\Hello315'))
        printList(findRelatedFiles('D:\\temp\\Hello315\\grails-app\\domain\\Book.groovy','D:\\temp\\Hello315'))
        printList(findRelatedFiles('D:\\temp\\Hello315\\grails-app\\views\\book\\index.gsp','D:\\temp\\Hello315'))
    }
}
