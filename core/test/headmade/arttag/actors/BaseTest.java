package headmade.arttag.actors;

import org.junit.BeforeClass;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Clipboard;

public class BaseTest {
	private static final String	TAG	= BaseTest.class.getName();

	@BeforeClass
	public static void init() {
		Gdx.app = new Application() {

			@Override
			public void setLogLevel(int logLevel) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removeLifecycleListener(LifecycleListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public void postRunnable(Runnable runnable) {
				// TODO Auto-generated method stub

			}

			@Override
			public void log(String tag, String message, Throwable exception) {
				System.err.println(tag + ": " + message + " ex: " + exception.getMessage());
			}

			@Override
			public void log(String tag, String message) {
				System.out.println(tag + ": " + message);
			}

			@Override
			public int getVersion() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public ApplicationType getType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Preferences getPreferences(String name) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Net getNet() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getNativeHeap() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getLogLevel() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public long getJavaHeap() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Input getInput() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Graphics getGraphics() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Files getFiles() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Clipboard getClipboard() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Audio getAudio() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ApplicationListener getApplicationListener() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void exit() {
				// TODO Auto-generated method stub

			}

			@Override
			public void error(String tag, String message, Throwable exception) {
				System.err.println(tag + ": " + message + " ex: " + exception.getMessage());

			}

			@Override
			public void error(String tag, String message) {
				System.err.println(tag + ": " + message);
			}

			@Override
			public void debug(String tag, String message, Throwable exception) {
				this.log(tag, message, exception);
			}

			@Override
			public void debug(String tag, String message) {
				this.log(tag, message);
			}

			@Override
			public void addLifecycleListener(LifecycleListener listener) {
				// TODO Auto-generated method stub

			}
		};
	}
}
