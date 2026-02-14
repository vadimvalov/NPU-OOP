package main.java.org.serialization;

import main.java.org.serialization.entities.Company;
import main.java.org.serialization.entities.Department;
import main.java.org.serialization.entities.User;
import main.java.org.serialization.security.CryptoUtil;
import main.java.org.serialization.security.EncryptedUser;
import main.java.org.serialization.serializers.*;
import main.java.org.serialization.versioning.*;

import javax.crypto.SecretKey;
import java.io.*;
import java.time.LocalDateTime;

public class Main {
    
    public static void main(String[] args) {
        printHeader("ADVANCED SERIALIZATION & DESERIALIZATION SYSTEM");
        
        // Выбор демонстрации
        if (args.length > 0) {
            runSpecificDemo(args[0]);
        } else {
            runAllDemos();
        }
    }
    
    /**
     * Запустить все демонстрации
     */
    private static void runAllDemos() {
        // Demo 1: Сложные графы объектов с циклическими ссылками
        demo1_ComplexObjectGraphs();
        
        // Demo 2: Шифрование чувствительных данных
        demo2_EncryptedSerialization();
        
        // Demo 3: Версионирование классов
        demo3_Versioning();
        
        // Demo 4: Кастомные сериализаторы
        demo4_CustomSerializers();
        
        // Demo 5: Комбинированная демонстрация
        demo5_CombinedFeatures();
        
        // Итоги
        printSummary();
    }
    
    /**
     * DEMO 1: Сложные графы объектов с циклическими ссылками
     */
    private static void demo1_ComplexObjectGraphs() {
        printHeader("DEMO 1: Complex Object Graphs with Circular References");
        
        System.out.println("Creating complex company structure with circular references...\n");
        
        // Создаём компанию
        Company techCorp = new Company("TechCorp International", "USA", "Software");
        techCorp.setRevenue(5_000_000_000.0);
        techCorp.setMarketCap(25_000_000_000.0);
        
        // Создаём департаменты
        Department engineering = new Department("Engineering", "San Francisco");
        engineering.setDescription("Software development and architecture");
        
        Department sales = new Department("Sales", "New York");
        sales.setDescription("Global sales and business development");
        
        Department hr = new Department("Human Resources", "Austin");
        hr.setDescription("Talent acquisition and employee relations");
        
        // Создаём поддепартамент (иерархия)
        Department backend = new Department("Backend Engineering", "San Francisco");
        engineering.addSubDepartment(backend);
        
        // Добавляем департаменты в компанию
        techCorp.addDepartment(engineering);
        techCorp.addDepartment(sales);
        techCorp.addDepartment(hr);
        
        // Создаём сотрудников
        User alice = new User("alice", "alice@techcorp.com", "SecurePass123!");
        alice.setCreditCardNumber("4532-1234-5678-9010");
        alice.setSsn("123-45-6789");
        
        User bob = new User("bob", "bob@techcorp.com", "BobPassword456!");
        bob.setCreditCardNumber("5425-2345-6789-0123");
        bob.setSsn("987-65-4321");
        
        User charlie = new User("charlie", "charlie@techcorp.com", "CharliePass789!");
        
        User diana = new User("diana", "diana@techcorp.com", "DianaSecure000!");
        
        // Добавляем в департаменты (создаёт циклы: User ↔ Department ↔ Company)
        engineering.addEmployee(alice);
        engineering.addEmployee(bob);
        backend.addEmployee(charlie);
        sales.addEmployee(diana);
        
        // Создаём дружеские связи (циклы: User ↔ User)
        alice.addFriend(bob);      // alice → bob, bob → alice (цикл!)
        bob.addFriend(charlie);    // bob → charlie, charlie → bob (цикл!)
        charlie.addFriend(diana);  // charlie → diana, diana → charlie (цикл!)
        diana.addFriend(alice);    // diana → alice, alice → diana (цикл!)
        
        // Устанавливаем CEO (ещё одна ссылка на существующего User)
        techCorp.setCEO(alice);
        
        // Выводим структуру
        System.out.println("Company structure created:");
        techCorp.printStructure();
        
        // Сериализуем граф
        System.out.println("\nSerializing complex object graph...");
        String filename = "techcorp_graph.ser";
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(techCorp);
            System.out.println("✅ Serialized to: " + filename);
            
            File file = new File(filename);
            System.out.println("   File size: " + file.length() + " bytes");
            
        } catch (IOException e) {
            System.err.println("❌ Serialization failed: " + e.getMessage());
            return;
        }
        
        // Десериализуем граф
        System.out.println("\nDeserializing object graph...");
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Company loadedCompany = (Company) ois.readObject();
            System.out.println("✅ Deserialized successfully");
            
            // Проверяем целостность
            System.out.println("\nVerifying graph integrity:");
            verifyGraphIntegrity(loadedCompany);
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Deserialization failed: " + e.getMessage());
        }
        
        System.out.println("\n" + "=".repeat(70));
    }
    
    /**
     * DEMO 2: Шифрование чувствительных данных
     */
    private static void demo2_EncryptedSerialization() {
        printHeader("DEMO 2: Encrypted Serialization of Sensitive Data");
        
        System.out.println("Creating user with sensitive data...\n");
        
        // Создаём пользователя с чувствительными данными
        EncryptedUser secureUser = new EncryptedUser(
            "john_secure",
            "john@example.com",
            "MyTopSecretPassword123!"
        );
        secureUser.setCreditCardNumber("4916-3385-0811-3645");
        secureUser.setSsn("555-12-3456");
        
        System.out.println("User created:");
        System.out.println("  " + secureUser.toSecureString());
        System.out.println();
        
        // Сериализация с автоматическим шифрованием
        String filename = "secure_user.enc";
        
        System.out.println("Serializing with automatic encryption...");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(secureUser);
            System.out.println("✅ Encrypted and serialized to: " + filename);
            
        } catch (IOException e) {
            System.err.println("❌ Serialization failed: " + e.getMessage());
            return;
        }
        
        // Проверяем, что данные зашифрованы в файле
        System.out.println("\nVerifying encryption (inspecting raw file):");
        inspectSerializedFile(filename, 300);
        
        // Десериализация с автоматической расшифровкой
        System.out.println("\nDeserializing with automatic decryption...");
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            EncryptedUser loadedUser = (EncryptedUser) ois.readObject();
            System.out.println("✅ Decrypted and deserialized successfully");
            
            System.out.println("\nDecrypted user data:");
            System.out.println("  Username: " + loadedUser.getUsername());
            System.out.println("  Email: " + loadedUser.getEmail());
            System.out.println("  Password: " + loadedUser.getPassword());
            System.out.println("  CC: " + CryptoUtil.mask(loadedUser.getCreditCardNumber(), 4));
            System.out.println("  SSN: " + CryptoUtil.mask(loadedUser.getSsn(), 4));
            
            // Проверяем пароль
            System.out.println("\nPassword verification:");
            System.out.println("  Correct password: " + loadedUser.verifyPassword("MyTopSecretPassword123!"));
            System.out.println("  Wrong password: " + loadedUser.verifyPassword("WrongPassword"));
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Deserialization failed: " + e.getMessage());
        }
        
        // Демонстрация криптографических утилит
        System.out.println("\nCryptographic utilities demonstration:");
        demonstrateCrypto();
        
        System.out.println("\n" + "=".repeat(70));
    }
    
    /**
     * DEMO 3: Версионирование классов
     */
    private static void demo3_Versioning() {
        printHeader("DEMO 3: Class Versioning (Backward & Forward Compatibility)");
        
        // Тест 1: V1 → V1
        System.out.println("TEST 1: UserV1 → serialize → deserialize as UserV1\n");
        
        UserV1 userV1 = new UserV1("legacy_user", "legacy@example.com", 35);
        System.out.println("Created V1: " + userV1);
        
        String fileV1 = "user_v1.ser";
        serializeObject(userV1, fileV1);
        
        UserV1 loadedV1 = deserializeObject(fileV1, UserV1.class);
        if (loadedV1 != null) {
            System.out.println("Loaded V1:  " + loadedV1);
            System.out.println("✅ V1 → V1 works perfectly\n");
        }
        
        // Тест 2: V2 → V2
        System.out.println("TEST 2: UserV2 → serialize → deserialize as UserV2\n");
        
        UserV2 userV2 = new UserV2(
            "modern_user",
            "modern@example.com",
            LocalDateTime.now().minusYears(28)
        );
        userV2.setPhoneNumber("+1-555-0123");
        userV2.setAddress("456 Innovation Blvd, Silicon Valley");
        userV2.setPremium(true);
        
        System.out.println("Created V2: " + userV2);
        
        String fileV2 = "user_v2.ser";
        serializeObject(userV2, fileV2);
        
        UserV2 loadedV2 = deserializeObject(fileV2, UserV2.class);
        if (loadedV2 != null) {
            System.out.println("Loaded V2:  " + loadedV2);
            System.out.println("✅ V2 → V2 works perfectly\n");
        }
        
        // Тест 3: V1 → V2 (backward compatibility через адаптер)
        System.out.println("TEST 3: UserV1 → convert to UserV2 (BACKWARD COMPATIBILITY)\n");
        
        System.out.println("Converting V1 to V2 using VersionAdapter...");
        UserV2 convertedUser = VersionAdapter.convertV1toV2(userV1);
        
        if (convertedUser != null) {
            System.out.println("Original V1: " + userV1);
            System.out.println("Converted:   " + convertedUser);
            System.out.println("✅ V1 → V2 conversion successful\n");
        }
        
        // Тест 4: V2 → V1 (forward compatibility с потерей данных)
        System.out.println("TEST 4: UserV2 → convert to UserV1 (FORWARD - DATA LOSS)\n");
        
        System.out.println("Converting V2 to V1 (data will be lost)...");
        UserV1 downgraded = VersionAdapter.convertV2toV1(userV2);
        
        if (downgraded != null) {
            System.out.println("Original V2: " + userV2);
            System.out.println("Downgraded:  " + downgraded);
            System.out.println("⚠️  Lost: phoneNumber, address, isPremium, dateOfBirth\n");
        }
        
        // Тест 5: Автоматическое определение версии
        System.out.println("TEST 5: Automatic version detection and migration\n");
        
        UserV2 autoMigrated = VersionAdapter.deserializeAsV2(fileV1);
        if (autoMigrated != null) {
            System.out.println("Auto-migrated: " + autoMigrated);
            System.out.println("✅ Automatic migration successful\n");
        }
        
        System.out.println("=".repeat(70));
    }
    
    /**
     * DEMO 4: Кастомные сериализаторы
     */
    private static void demo4_CustomSerializers() {
        printHeader("DEMO 4: Custom Serializers");
        
        // Создаём тестовую компанию
        Company company = createTestCompany();
        
        // CustomSerializer с метаданными
        System.out.println("Using CustomSerializer with metadata...\n");
        
        CustomSerializer customSerializer = new CustomSerializer(true);
        
        CustomSerializer.Metadata metadata = new CustomSerializer.Metadata(
            company.getClass().getName(),
            "3.0",
            System.currentTimeMillis()
        );
        metadata.setDescription("Production company data with full employee roster");
        
        CustomSerializer.SerializationStats stats = 
            customSerializer.serializeWithMetadata(company, "company_custom.ser", metadata);
        
        System.out.println("\nSerialization statistics:");
        System.out.println("  " + stats);
        
        CustomSerializer.DeserializationResult result = 
            customSerializer.deserializeWithMetadata("company_custom.ser");
        
        if (result.isSuccess()) {
            System.out.println("\nDeserialized successfully:");
            System.out.println("  Metadata: " + result.getMetadata());
            System.out.println("  Object: " + result.getObject().getClass().getSimpleName());
        }
        
        System.out.println();
        
        // SecureSerializer
        System.out.println("Using SecureSerializer with encryption...\n");
        
        SecureSerializer secureSerializer = new SecureSerializer();
        secureSerializer.setLogging(true);
        
        try {
            secureSerializer.serialize(company, "company_secure.enc");
            
            System.out.println("\nEncryption key (for sharing): ");
            String key = secureSerializer.exportKey();
            System.out.println("  " + (key.length() > 50 ? key.substring(0, 50) + "..." : key));            
            Company decrypted = (Company) secureSerializer.deserialize("company_secure.enc");
            System.out.println("\nDecrypted: " + decrypted.getName());
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
        
        System.out.println();
        
        // GraphSerializer
        System.out.println("Using GraphSerializer for complex graphs...\n");
        
        GraphSerializer graphSerializer = new GraphSerializer(true);
        
        try {
            graphSerializer.serializeGraph(company, "company_graph.ser");
            Company loaded = (Company) graphSerializer.deserializeGraph("company_graph.ser");
            
            System.out.println("\nGraph deserialized:");
            loaded.printStructure();
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
        
        System.out.println("=".repeat(70));
    }
    
    /**
     * DEMO 5: Комбинированная демонстрация всех возможностей
     */
    private static void demo5_CombinedFeatures() {
        printHeader("DEMO 5: Combined Features Showcase");
        
        System.out.println("Creating enterprise-grade serialization scenario...\n");
        
        // 1. Создаём сложный граф
        Company enterprise = new Company("GlobalTech Enterprises", "Switzerland", "Technology");
        enterprise.setRevenue(50_000_000_000.0);
        
        Department engineering = new Department("Global Engineering", "Zurich");
        Department security = new Department("Security & Compliance", "Geneva");
        
        enterprise.addDepartment(engineering);
        enterprise.addDepartment(security);
        
        // 2. Создаём пользователей с чувствительными данными
        EncryptedUser ceo = new EncryptedUser("sarah_ceo", "sarah@globaltech.com", "CEO_SecurePass_2024!");
        ceo.setCreditCardNumber("5555-4444-3333-2222");
        ceo.setSsn("999-88-7777");
        
        User cto = new User("mike_cto", "mike@globaltech.com", "CTO_Password!");
        User secOfficer = new User("anna_security", "anna@globaltech.com", "Security123!");
        
        engineering.addEmployee(cto);
        security.addEmployee(secOfficer);
        
        ceo.setDepartment(engineering);
        
        System.out.println("Enterprise structure:");
        System.out.println("  Company: " + enterprise.getName());
        System.out.println("  Departments: " + enterprise.getDepartments().size());
        System.out.println("  Total employees: " + enterprise.getTotalEmployeeCount());
        System.out.println();
        
        // 3. Используем комбинацию сериализаторов
        System.out.println("Step 1: Serialize with CustomSerializer (metadata)");
        
        CustomSerializer customSer = new CustomSerializer(true);
        CustomSerializer.Metadata meta = new CustomSerializer.Metadata(
            "EnterpriseData",
            "4.0",
            System.currentTimeMillis()
        );
        meta.setDescription("Confidential enterprise data - handle with care");
        
        customSer.serializeWithMetadata(enterprise, "enterprise_step1.ser", meta);
        
        System.out.println("\nStep 2: Re-serialize with SecureSerializer (encryption)");
        
        try {
            CustomSerializer.DeserializationResult result = 
                customSer.deserializeWithMetadata("enterprise_step1.ser");
            
            if (result.isSuccess()) {
                SecureSerializer secureSer = new SecureSerializer();
                secureSer.setLogging(true);
                secureSer.serialize(result.getObject(), "enterprise_final.enc");
                
                System.out.println("\n✅ Combined serialization complete!");
                System.out.println("   - Metadata preserved");
                System.out.println("   - Data encrypted");
                System.out.println("   - Complex graph handled");
                
                // Проверяем размеры файлов
                System.out.println("\nFile comparison:");
                System.out.println("  Regular: " + new File("enterprise_step1.ser").length() + " bytes");
                System.out.println("  Encrypted: " + new File("enterprise_final.enc").length() + " bytes");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
        
        System.out.println("\n" + "=".repeat(70));
    }
    
    // ========== HELPER METHODS ==========
    
    private static void verifyGraphIntegrity(Company company) {
        System.out.println("  ✓ Company name: " + company.getName());
        System.out.println("  ✓ Departments: " + company.getDepartments().size());
        System.out.println("  ✓ Total employees: " + company.getTotalEmployeeCount());
        
        // Проверяем циклические ссылки
        if (!company.getDepartments().isEmpty()) {
            Department dept = company.getDepartments().get(0);
            if (!dept.getEmployees().isEmpty()) {
                User user = dept.getEmployees().get(0);
                
                System.out.println("\n  Circular reference checks:");
                System.out.println("    User → Department: " + 
                    (user.getDepartment() == dept ? "✅ OK" : "❌ BROKEN"));
                System.out.println("    Department → Company: " + 
                    (dept.getCompany() == company ? "✅ OK" : "❌ BROKEN"));
                
                // Проверяем дружбу
                if (!user.getFriends().isEmpty()) {
                    User friend = user.getFriends().get(0);
                    System.out.println("    User ↔ User friendship: " + 
                        (friend.getFriends().contains(user) ? "✅ OK" : "❌ BROKEN"));
                }
            }
        }
        
        // Проверяем CEO
        if (company.getCeo() != null) {
            System.out.println("    CEO reference: ✅ " + company.getCeo().getUsername());
        }
    }
    
    private static void demonstrateCrypto() {
        SecretKey key = CryptoUtil.generateKey();
        
        String plaintext = "Top Secret Information";
        String encrypted = CryptoUtil.encrypt(plaintext, key);
        String decrypted = CryptoUtil.decrypt(encrypted, key);
        
        System.out.println("  Encryption demo:");
        System.out.println("    Plaintext: " + plaintext);
        System.out.println("    Encrypted: " + encrypted.substring(0, 40) + "...");
        System.out.println("    Decrypted: " + decrypted);
        System.out.println("    Match: " + plaintext.equals(decrypted));
        
        String password = "MyPassword123";
        String hash = CryptoUtil.hashPassword(password);
        
        System.out.println("\n  Password hashing demo:");
        System.out.println("    Password: " + password);
        System.out.println("    Hash: " + hash.substring(0, 40) + "...");
        System.out.println("    Verify correct: " + CryptoUtil.verifyPassword(password, hash));
        System.out.println("    Verify wrong: " + CryptoUtil.verifyPassword("WrongPass", hash));
    }
    
    private static void inspectSerializedFile(String filename, int maxBytes) {
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] buffer = new byte[maxBytes];
            int bytesRead = fis.read(buffer);
            
            System.out.print("  First " + bytesRead + " bytes (hex): ");
            for (int i = 0; i < Math.min(50, bytesRead); i++) {
                System.out.printf("%02X ", buffer[i]);
            }
            System.out.println("...");
            
            // Проверяем наличие открытых данных
            String content = new String(buffer, 0, bytesRead);
            boolean hasPassword = content.contains("password") || content.contains("Password");
            boolean hasCreditCard = content.contains("4916") || content.contains("5555");
            
            System.out.println("  Contains 'password': " + (hasPassword ? "❌ NOT ENCRYPTED!" : "✅ Encrypted"));
            System.out.println("  Contains CC number: " + (hasCreditCard ? "❌ NOT ENCRYPTED!" : "✅ Encrypted"));
            
        } catch (IOException e) {
            System.err.println("  ❌ Could not inspect file: " + e.getMessage());
        }
    }
    
    private static Company createTestCompany() {
        Company company = new Company("StartupCo", "USA", "SaaS");
        
        Department dev = new Department("Development", "Remote");
        company.addDepartment(dev);
        
        User dev1 = new User("dev1", "dev1@startup.com", "pass1");
        User dev2 = new User("dev2", "dev2@startup.com", "pass2");
        
        dev.addEmployee(dev1);
        dev.addEmployee(dev2);
        
        dev1.addFriend(dev2);
        
        company.setCEO(dev1);
        
        return company;
    }
    
    private static <T> void serializeObject(T object, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(object);
            System.out.println("  ✅ Serialized to: " + filename);
        } catch (IOException e) {
            System.err.println("  ❌ Serialization failed: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private static <T> T deserializeObject(String filename, Class<T> clazz) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            T object = (T) ois.readObject();
            System.out.println("  ✅ Deserialized from: " + filename);
            return object;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("  ❌ Deserialization failed: " + e.getMessage());
            return null;
        }
    }
    
    private static void runSpecificDemo(String demoName) {
        switch (demoName.toLowerCase()) {
            case "graphs":
            case "1":
                demo1_ComplexObjectGraphs();
                break;
            case "encryption":
            case "2":
                demo2_EncryptedSerialization();
                break;
            case "versioning":
            case "3":
                demo3_Versioning();
                break;
            case "serializers":
            case "4":
                demo4_CustomSerializers();
                break;
            case "combined":
            case "5":
                demo5_CombinedFeatures();
                break;
            default:
                System.out.println("Unknown demo: " + demoName);
                System.out.println("\nAvailable demos:");
                System.out.println("  1 or 'graphs'       - Complex object graphs");
                System.out.println("  2 or 'encryption'   - Encrypted serialization");
                System.out.println("  3 or 'versioning'   - Class versioning");
                System.out.println("  4 or 'serializers'  - Custom serializers");
                System.out.println("  5 or 'combined'     - Combined features");
                break;
        }
    }
    
    private static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println(centerText(title, 70));
        System.out.println("=".repeat(70) + "\n");
    }
    
    private static void printSummary() {
        printHeader("DEMONSTRATION SUMMARY");
        
        System.out.println("✅ All demonstrations completed successfully!\n");
        
        System.out.println("Features demonstrated:");
        System.out.println("  1. ✅ Complex object graphs with circular references");
        System.out.println("  2. ✅ Automatic encryption/decryption of sensitive data");
        System.out.println("  3. ✅ Class versioning (backward & forward compatibility)");
        System.out.println("  4. ✅ Custom serializers with metadata and statistics");
        System.out.println("  5. ✅ Secure serialization with AES-256-GCM encryption");
        System.out.println("  6. ✅ Graph serialization with cycle detection");
        
        System.out.println("\nGenerated files:");
        listGeneratedFiles();
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println(centerText("Thank you for using the Serialization System!", 70));
        System.out.println("=".repeat(70) + "\n");
    }
    
    private static void listGeneratedFiles() {
        String[] files = {
            "techcorp_graph.ser",
            "secure_user.enc",
            "user_v1.ser",
            "user_v2.ser",
            "company_custom.ser",
            "company_secure.enc",
            "company_graph.ser",
            "enterprise_step1.ser",
            "enterprise_final.enc"
        };
        
        for (String filename : files) {
            File file = new File(filename);
            if (file.exists()) {
                System.out.println("  📄 " + filename + " (" + file.length() + " bytes)");
            }
        }
    }
    
    private static String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }
}