/**
 *
 */
public class Testi {
/*
    private String frontMatter =
            "---\n" + "layout: \"custom\"\n" + "title: \"My Title\"\n" + "my_list:\n" + "  - one\n" + "  - two\n"
                    + "  - three\n" + "---\n";


    FileSystem  fs;

    @Before
    public void before() {
        fs = Jimfs.newFileSystem(Configuration.unix());
    }

    @After
    public void after() throws IOException {
        fs.close();
    }


    @Test
    public void bla() throws IOException {
        // For a simple file system with Unix-style paths and behavior:

        Path workingDirectory = fs.getPath("/");
        final Path s = Files.createDirectories(workingDirectory.resolve("source/layouts"));

        final Path layoutFtl = Files.write(s.resolve("layout.ftl"), frontMatter.getBytes());
        final Path mySecondFtl = Files.write(s.resolve("mySecond.fTl"), "---     ---".getBytes());
        final Path myThirdFtl = Files.write(s.resolve("_myThird.ftl"), frontMatter.getBytes());
        final Path fourthFtl = Files.write(s.resolve("ble_fourth.ftl"), frontMatter.getBytes());

        final List<Layout> layouts = Layouts.find(workingDirectory);
        assertEquals(3, layouts.size());
    }


    private String template =
            "---\n" + "layout: \"custom\"\n" + "title: \"My Title\"\n" + "my_list:\n" + "  - one\n" + "  - two\n"
                    + "  - three\n" + "---\n    <html>[@saito.yield/][@saito.partial name=\"footer\"/]</html>";



    @Test
    public void t() throws IOException, TemplateException {

        Path workingDirectory = fs.getPath("/");
        final Path s = Files.createDirectories(workingDirectory.resolve("source/layouts"));

        final Path layoutFtl = Files.write(s.resolve("layout.ftl"), template.getBytes());


        final List<Layout> layouts = Layouts.find(workingDirectory);
        assertEquals(1, layouts.size());

        final Layout l = layouts.get(0);
        System.out.println(l);

        assertEquals("<html>[@saito.yield/][@saito.partial name=\"footer\"/]</html>", l.getTemplate());

        Map<String, Object> map = new HashMap<>();

        *//*ObjectMapper mapper = new ObjectMapper();
        map = mapper.readValue(friends, new org.codehaus.jackson.type.TypeReference<HashMap<String,Object>>() {
        });*//*

        final Template template = new Template(l.getName(), l.getTemplate(), FreemarkerConfig.INSTANCE.cfg);
        final Map<String, Object> data = new HashMap<>();
        data.put("_saito_content_", "<p>Hallo was geht</p>");
        StringWriter writer = new StringWriter();
        template.process(data, writer);
        System.out.println("===============>" + writer.toString());

    }*/


}
