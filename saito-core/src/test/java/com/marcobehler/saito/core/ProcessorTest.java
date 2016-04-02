package com.marcobehler.saito.core;

/**
 * @author Marco Behler <marco@marcobehler.com>
 */
public class ProcessorTest {


  /*  @Test
    public void build() throws IOException {

        String layout = Resources.toString(Resources.getResource(Processor.class, "/layout.ftl"), Charsets.UTF_8);
        Path layoutPath = fs.getPath("/").resolve("layout.ftl");
        Files.write(layoutPath, layout.getBytes());

        String template = Resources.toString(Resources.getResource(Processor.class, "/index.html.ftl"), Charsets.UTF_8);
        Path templatePath = fs.getPath("/").resolve("index.html.ftl");
        Files.write(templatePath, template.getBytes());

        SaitoModel m = new SaitoModel(sourceDirectory);
        m.getTemplates().add(new Template(templatePath));
        m.getLayouts().add(new Layout(layoutPath));

        List<Output> output = new Processor().process(m);
        assertThat(output.size()).isEqualTo(1);
        assertThat(output.get(0).getOutput()).isEqualToIgnoringWhitespace("<html>\n" +
                "<head>\n" +
                "    <title>My Sample Site</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <p>Hello World</p></body>\n" +
                "</html>");
    }*/
}
