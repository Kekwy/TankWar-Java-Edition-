//import com.kekwy.tankwar.io.LoginAction;
//import com.kekwy.tankwar.io.Package;
//import com.kekwy.tankwar.io.GameAction;
//import org.junit.jupiter.api.Test;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.ObjectOutputStream;
//import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.SocketChannel;
//
//public class ServerTest {
//
//	@Test
//	void LoginTest() throws IOException {
//		SocketChannel channel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 2727));
//		Package p = new Package();
//		LoginAction login = new LoginAction();
//		login.init("Kekwy", "201102");
//		p.init(GameAction.NUMBER_LOGIN, login);
//
//		ByteArrayOutputStream bAos = new ByteArrayOutputStream();
//		ObjectOutputStream oos = new ObjectOutputStream(bAos);
//
//		oos.writeObject(p);
//		ByteBuffer buffer = ByteBuffer.wrap(bAos.toByteArray());
//		channel.write(buffer);
//
//		bAos.close();
//		oos.close();
//
//	}
//
//}
