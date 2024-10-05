import core.KVEngine;
import obj.Mvcc;
import obj.Transaction;

/**
 * @Author: liyanlong
 * @Date: 2024-10-04 18:37
 **/

public class Main {

    public static KVEngine kvEngine = new KVEngine();

    public static void main(String[] args) {

        Mvcc mvcc = new Mvcc(kvEngine);
        Transaction tx0 = mvcc.begin_transaction();

        tx0.set("a".getBytes(), "a1".getBytes());
        tx0.set("b".getBytes(), "b1".getBytes());
        tx0.set("c".getBytes(), "c1".getBytes());
        tx0.set("d".getBytes(), "d1".getBytes());
        tx0.set("e".getBytes(), "e1".getBytes());
        tx0.commit();
        //tx0.printAll(); //a=a1 b=b1 c=c1 d=d1 e=e1

        Transaction tx1 = mvcc.begin_transaction();
        tx1.set("a".getBytes(), "a2".getBytes());
        tx1.set("e".getBytes(), "e2".getBytes());
        // t1 虽然未提交，但是能看到自己的修改了
        //a=a2 b=b1 c=c1 d=d1 e=e2
        tx1.printAll();

        Transaction tx2 = mvcc.begin_transaction();
        tx2.delete("b".getBytes());
        // 此时 T1 没提交，所以 T2 看到的是
        tx2.printAll(); // a=a1 c=c1 d=d1 e=e1
        tx1.commit();
        // 此时 T2 仍然看不到 T1 的提交，因为 T2 开启的时候，T2 还没有提交（可重复读）
        tx2.printAll(); // a=a1 c=c1 d=d1 e=e1

        Transaction tx3 = mvcc.begin_transaction();
        // T3 能看到 T1 的提交，但是看不到 T2 的提交
        tx3.printAll(); // a=a2 b=b1 c=c1 d=d1 e=e2
        //T3写新数据
        tx3.set("f".getBytes(), "f1".getBytes());
        byte[] bytes = tx3.get("f".getBytes());
        System.out.println("f=" + new String(bytes));
        try {
            //T2写一样的数据 报错
            tx2.set("f".getBytes(), "f1".getBytes());
        } catch (Exception e) {
//            e.printStackTrace();
            tx2.rollback();
            //T2 回滚 仍然能看不到 T1 的提交，因为T2是在T1提交前创建
            tx2.printAll(); //a=a1 b=b1 c=c1 d=d1 e=e1
        }
        tx3.commit();
        // T3 提交后看到的是T1和T3的结果
        tx3.printAll(); //a=a2 b=b1 c=c1 d=d1 e=e2 f=f1

    }

}
