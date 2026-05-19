package com.example.springboot.init;

import com.example.springboot.entity.Product;
import com.example.springboot.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.util.StopWatch;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    // コンストラクタ注入（引数1つのため@Autowiredは省略）
    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Override
    @Transactional // 👈 ここで遅延評価のバケツが作られる
    public void run(String... args) throws Exception {

        log.info("====== 🚀 100万件のテストデータ挿入処理を開始します ======");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start(); // ⏱️ 計測開始

        // 1. 100万件のデータを「生成」する
        int totalRecords = 1000;

        // 💡 拡張する余計なコストを防ぐため、最初から「100万個入るサイズ」の箱としてListを生成
        List<Product> hugeProductList = new ArrayList<>(totalRecords);

        for (int i = 1; i <= totalRecords; i++) {
            Product product = new Product();
            // id は SEQUENCE（またはJava側で手動セット）なのでここではセットしない
            product.setName("テスト商品_" + i);
            product.setPrice(100 + (i % 1000)); // 適当な金額
            product.setStock(i % 50);           // 適当な在庫数
            product.setUuidV4(UUID.randomUUID()); // 一意のUUID

            hugeProductList.add(product);
        }

        productRepository.saveAll(hugeProductList);

        log.info("====== 🏁 saveAll() 完了。これからDBに一括送信（コミット）します ======");

        // 💡 2. メソッドを抜ける直前に、明示的に flush() してここでDBにドカンと送る！
        // これにより、コミット（一括送信）にかかった本当の時間をピンポイントで計測できます。
        productRepository.flush();

        stopWatch.stop(); // ⏱️ 計測終了

        log.info("==========================================================");
        log.info("📊 【一括挿入リザルトレポート】");
        log.info("総処理時間: {} ms (約 {} 秒)", stopWatch.getTotalTimeMillis(), stopWatch.getTotalTimeSeconds());
        log.info("==========================================================");
    }
}