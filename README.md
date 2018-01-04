[![](https://jitpack.io/v/Ahmed-Adel-Ismail/ActorLite.svg)](https://jitpack.io/#Ahmed-Adel-Ismail/ActorLite)

# ActorLite
A Light weight Actor Model library that helps communication between Android Components in a Message Driven manner

# How It Works
For every class that implements the <b>Actor</b> interface, it registers itself to the <b>ActorSystem</b>, which is responsible for delivering messages between the registered Actors through there address, the address of any Actor is the <b>Class</b> of it, for example the address of the <b>MainActivity</b> is <b>MainActivity.class</b>, and so on
	
You do not have to hold reference to Any Object any more, just send by the Object/Actor address and it will be received and executed on that Object's favorite thread ... you dont have to worry about multi-threading or references any more
	
To register an Actor to the Actor system, you either extend one of the available classes, or do it manually ... this will be explained in the coming section

# Getting Started - Setup Actors

# Step 1. add ActorLite to your Application's onCreate() method 

In this step, you will cause any <b>Actvity</b> and any <b>android.support.v4.app.Fragment</b> that implements the <b>Actor</b> interface to automatically register and unregister itself to the <b>ActorSystem</b>

    @Override
    public void onCreate() {
        super.onCreate();
        ActorLite.with(this);
    }

# Step 2. add Activities as Actors
For Activities, all you have to do is implement the Actor interface, like the following :

    public class MainActivity extends AppCompatActivity implements Actor {

		public static final int MESSAGE_ID_DO_SOMETHING = 1938;

		...

		@NonNull
		@Override
		public Scheduler observeOnScheduler() {
			// specify the Thread that onMessageReceived()
			// will be executed on, in Activities 
			// and Fragments it should be the 
			// Main Thread
			return AndroidSchedulers.mainThread();
		}


		@Override
		public void onMessageReceived(Message message) {
			if(message.getId() == MESSAGE_ID_DO_SOMETHING){
				// handle message on the Thread 
				// specified in observeOnScheduler() 
			}
		}

	}

# Step 3. add Fragments (non-support fragments) as Actors
For non-support Fragments, you either need to extend the ActorFragment, or you will register it manually, remember that android.support.v4.app.Fragment that implements Actor interface is registered and unregistered by default for you ... let us take the easy way first :

	public class MainFragment extends ActorFragment {

		public static final int MESSAGE_ID_DO_SOMETHING = 1236;

		...

		@NonNull
		@Override
		public Scheduler observeOnScheduler() {
			// specify the Thread that onMessageReceived()
			// will be executed on, in Activities 
			// and Fragments it should be the 
			// Main Thread
			return AndroidSchedulers.mainThread();
		}


		@Override
		public void onMessageReceived(Message message) {
			if(message.getId() == MESSAGE_ID_DO_SOMETHING){
				// handle message on the Thread 
				// specified in observeOnScheduler() 
			}
		}
	}

Nearly the same thing done in Activity is done in Fragment

# Step 4. add Services as Actors
For Services, you either need to extend the ActorService, or you will register it manually ... let us take the easy way here too :

	public class MainService extends ActorService {

		public static final int MESSAGE_ID_DO_SOMETHING = 1456;

		...

		@NonNull
		@Override
		public Scheduler observeOnScheduler() {
			// specify the Thread that onMessageReceived()
			// will be executed on, in Services 
			// it is more likely to be a background
			// thread since we need to do stuff 
			// that wont update the UI
			return Schedulers.computation();
		}


		@Override
		public void onMessageReceived(Message message) {
			if(message.getId() == MESSAGE_ID_DO_SOMETHING){
				// handle message on the Thread 
				// specified in observeOnScheduler() 
			}
		}
	}
	
# Step 5. add Application class as an Actor
The Application class itself will be an Actor if it just implemented the <b>Actor</b> interface, and you can send to it Messages as well as any other Actor, all you need to do is implement the <b>Actor</b> interface

	public class MainApp extends Application implements Actor {

		public static final int MESSAGE_ID_DO_SOMETHING = 1626;
	
		@Override
		public void onCreate() {
			super.onCreate();
			ActorLite.with(this);
		}

		@NonNull
		@Override
		public Scheduler observeOnScheduler() {
			// specify the Thread that onMessageReceived()
			// will be executed on, in Application 
			// class it is safer to make it in background
			// thread since we need to do stuff 
			// that wont update the UI
			return Schedulers.computation();
		}


		@Override
		public void onMessageReceived(Message message) {
			if(message.getId() == MESSAGE_ID_DO_SOMETHING){
				// handle message on the Thread 
				// specified in observeOnScheduler() 
			}
		}
	}

# Step 6. add any Object as an Actor
For Any Object it should register and unregister itself manually from the <b>ActorSystem</b> and cancel all the pending Messages in the <b>ActorScheduler</b>, so for any Object that will be an Actor, it should have an initialization point, and a destruction point (similar to onCreate() and onDestroy() in the life-Cycle methods), so an example of an Actor Object will be as follows :

	public class ActorObject implements Actor {

		public ActorObject() {
			ActorSystem.register(this);
		}

		@NonNull
		@Override
		public Scheduler observeOnScheduler() {
			return Schedulers.computation();
		}


		@Override
		public void onMessageReceived(Message message) {
			// ...
		}

		public void onDestroy() {
			ActorSystem.unregister(this);
			ActorScheduler.cancel(getClass());
		}
	}
	
# Sending a message to an actor

			MyCustomObject myCustomObject = ...;
			Message message = new Message(MainFragment.MESSAGE_ID_DO_SOMETHING, myCustomObject);
			ActorSystem.send(message, MainFragment.class);
		
If the <b>MainFragment</b> unregistered itself from <b>ActorSystem</b> before the message is sent, nothing will happen

# Sending a delayed message to an actor

			MyCustomObject myCustomObject = ...;
			Message message = new Message(MainFragment.MESSAGE_ID_DO_SOMETHING, myCustomObject);
			ActorScheduler.after(5000) // 5000 milliseconds
				.send(message, MainFragment.class); 
		
If the <b>MainFragment</b> unregistered itself from <b>ActorSystem</b> before the message is sent, nothing will happen

# Setup android components as actors manually
Remember that you do not need to setup Activities Manually in all cases, so If you choose to Register and Unregister The remaining Android components manually, here is what to be done in every type :

# Setup Fragments (non-support Fragments) Manually

	public class MyActorFragment extends Fragment implements Actor {

		@CallSuper
		@Override
		public void onStart() {
			super.onStart();
			ActorSystem.register(this);
		}

		@CallSuper
		@Override
		public void onStop() {
		    ActorSystem.postpone(this);
			super.onStop();
		}

		@CallSuper
		@Override
		public void onDestroy() {
		    ActorSystem.unregister(this);
			if (getActivity() == null || getActivity().isFinishing()) {
				ActorScheduler.cancel(getClass());
			}
			super.onDestroy();
		}
	}

# Setup Service Manually

	public abstract class MyActorService extends Service implements Actor {


		@CallSuper
		@Override
		public void onCreate() {
			super.onCreate();
			ActorSystem.register(this);
		}

		@CallSuper
		@Override
		public void onDestroy() {
			ActorSystem.unregister(this);
			ActorScheduler.cancel(getClass());
			super.onDestroy();
		}

		...
	}

# Tips

To Avoid the big if/else blocks in onMessageReceived(), you can use <b>CommandsMap</b> instead ( https://github.com/Ahmed-Adel-Ismail/CommandsMap ), it is also used in the sample application in this repository

Also this library targets Java 8, which is possible through using the new Gradle setup supported in Android Studio 3, or by using retrolambda in older versions

# Gradle Dependencies

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.Ahmed-Adel-Ismail:ActorLite:0.0.5'
	}
