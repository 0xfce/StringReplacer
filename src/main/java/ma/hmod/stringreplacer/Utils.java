package ma.hmod.stringreplacer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {

	private Gui gui = new Gui();
	
    public void replace(String extractPath, File target, File out, String newStr, String oldStr, boolean equals, boolean caseSensetive, boolean replaceWholeWord) throws IOException {
        Map<String, String> packages = new HashMap<>();
        try(FileInputStream fileIn = new FileInputStream(target.getAbsoluteFile()); BufferedInputStream buffIn = new BufferedInputStream(fileIn); ZipInputStream in = new ZipInputStream(buffIn)) {
            ZipEntry entry;
            while((entry = in.getNextEntry()) != null) {

                if (entry.getName().endsWith(".class") && !entry.isDirectory()) {

                    ClassNode classNode = new ClassNode();
                    ClassReader classReader = new ClassReader(in);
                    classReader.accept(classNode, 0);

                    packages.put(classNode.name.substring(classNode.name.lastIndexOf("/") + 1).concat(".class"), classNode.name.substring(0, classNode.name.lastIndexOf("/")+1));


                    for (MethodNode method : classNode.methods) {
                        if (method != null) {
                            for (AbstractInsnNode node : method.instructions.toArray()) {
                                if (node instanceof LdcInsnNode) {
                                    LdcInsnNode str = (LdcInsnNode) node;
                                    if(caseSensetive ? (equals ? str.cst.toString().equals(oldStr) : str.cst.toString().contains(oldStr)) : (equals ? str.cst.toString().toLowerCase().equals(oldStr) : str.cst.toString().toLowerCase().contains(oldStr))) {
                                    	debug("\nString found in " + classNode.name.concat(".class"));
                                        InsnList list = new InsnList();
                                        list.add(new LdcInsnNode(replaceWholeWord ? newStr : (caseSensetive ? str.cst.toString().replace(oldStr, newStr) : str.cst.toString().toLowerCase().replace(oldStr, newStr))));
                                        method.instructions.insertBefore(node, list);
                                        method.instructions.remove(node);
                                        debug("REPLACED FROM: " + oldStr + " INTO -> " + newStr + "\n");
                                    }
//                                    System.out.println(str.cst);
                                }
                            }
                        }
                    }

                    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                    classNode.accept(classWriter);
                    outClass(extractPath, classNode.name, classWriter.toByteArray());

                }

            }
        }
        extractResources(packages, extractPath, target);
        outJar(packages, out.getAbsolutePath(), new File(extractPath).listFiles());
    }

    private void outClass(String extractPath, String name, byte[] bytes) throws IOException {
        name = name.substring(name.lastIndexOf("/") + 1).concat(".class");
        FileOutputStream out = new FileOutputStream(extractPath + name);
        out.write(bytes);
        out.close();
        debug(name + " has been written.");
    }

    private void outJar(Map<String, String> packg, String outJar, File[] files) throws IOException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
        JarOutputStream out = new JarOutputStream(new FileOutputStream(outJar), manifest);
        Arrays.stream(files).forEach(file -> {
            if(file != null && !file.isDirectory() && packg.containsKey(file.getName())) {
                try {
                    ZipEntry zipEntry = new ZipEntry(packg.get(file.getName())+file.getName());
                    zipEntry.setTime(file.lastModified());
                    out.putNextEntry(zipEntry);
                    FileInputStream in = new FileInputStream(file);
                    byte[] buffer = new byte[1024];
                    int size;
                    while ((size = in.read(buffer)) != -1) out.write(buffer, 0, size);
                    in.close();
                    out.closeEntry();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        out.close();
        System.out.println("Jar out in: " + outJar);
    }

    private void extractResources(Map<String, String> packages, String path, File target) throws IOException {
        JarInputStream jin = new JarInputStream(new FileInputStream(target));
        ZipEntry entry;
        while((entry = jin.getNextEntry()) != null) {
            if(!entry.getName().endsWith(".class") && entry.getName().contains(".")) {
            	debug(entry.getName().substring(entry.getName().lastIndexOf("/")+1) + " has been written.");
                FileOutputStream out = new FileOutputStream(path+entry.getName().substring(entry.getName().lastIndexOf("/")+1));
                packages.put(entry.getName().substring(entry.getName().lastIndexOf("/")+1), entry.getName().substring(0, entry.getName().lastIndexOf("/")+1));
                byte[] buff = new byte[1024];
                int length;
                while((length = jin.read(buff)) != -1) out.write(buff, 0, length);
                out.close();
            }
        }
        jin.close();
    }
    
    private void debug(String msg) {
    	System.out.println("[REPLACER] " + msg);
//    	this.gui.console.append("\n" + msg);
    }

}
