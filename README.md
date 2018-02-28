[![](https://jitpack.io/v/Ahmed-Adel-Ismail/ActorLite.svg)](https://jitpack.io/#Ahmed-Adel-Ismail/ActorLite)

# ActorLite
A Light weight Actor Model library that helps communication between Android Components in a Message Driven manner

# How It Works
For every class that implements the <b>Actor</b> interface, it registers itself to the <b>ActorSystem</b>, which is responsible for delivering messages between the registered Actors through there address, the address of any Actor is the <b>Class</b> of it, for example the address of the <b>MainActivity</b> is <b>MainActivity.class</b>, and so on
	
You do not have to hold reference to Any Object any more, just send by the Object/Actor address and it will be received and executed on that Object's favorite thread ... you don't have to worry about multi-threading or references any more
	
To register an Actor to the Actor system, you either extend one of the available classes, or do it manually ... this will be explained in the coming section

# Getting Started - Setup Actors

# Integrate ActorLite to your Application's onCreate() method

In this step, you will cause any <b>Actvity</b> and any <b>android.support.v4.app.Fragment</b> that implements the <b>Actor</b> interface to automatically register and unregister itself to the <b>ActorSystem</b>

```java
@Override
public void onCreate() {
    super.onCreate();
    ActorLite.with(this);
}
```

you can override the the default configuration for the Actor-System through ActorSystemConfiguration :

```java
@Override
public void onCreate() {
    super.onCreate();
    ActorLite.with(this, actorSystemConfiguration());
    ...
}

// these are the default configurations :
private ActorSystemConfiguration actorSystemConfiguration() {
    return new ActorSystemConfiguration.Builder()
            .registerActors(RegistrationStage.ON_START)
            .unregisterActors(UnregistrationStage.ON_DESTROY)
            .postponeMailboxOnStop(true)
            .build();
}
```

# Register Activities or Support Fragments as Actors

For Activities and Support Fragments, all you have to do is implement the Actor interface, and they will be registered / un-registered for you based on the configurations, like the following :

```java
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
```

# Register Services as Actors

For Services, you either need to extend the ActorService, or you will register it manually, notice that the Actor-System configuration will not affect Services ... let us take the easy way here and extend <b>ActorService</b> :

```java
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
```
	
# Register Application class as an Actor

The Application class itself can be an Actor if it implemented the <b>Actor</b> interface, and you can send to it Messages as well as any other Actor, all you need to do is implement the <b>Actor</b> interface

```java
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
```

# Register any Object as an Actor

For Any Object it should register and unregister itself manually from the <b>ActorSystem</b> and cancel all the pending Messages in the <b>ActorScheduler</b>, so for any Object that will be an Actor, it should have an initialization point, and a destruction point (similar to onCreate() and onDestroy() in the life-Cycle methods), so an example of an Actor Object will be as follows :

```java
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
```

# Sending a message to an actor

```java
MyCustomObject myCustomObject = ...;
Message message = new Message(MainFragment.MESSAGE_ID_DO_SOMETHING, myCustomObject);
ActorSystem.send(message, MainFragment.class);
```

If the <b>MainFragment</b> unregistered itself from <b>ActorSystem</b> before the message is sent, nothing will happen

# Sending a delayed message to an actor

```java
MyCustomObject myCustomObject = ...;
Message message = new Message(MainFragment.MESSAGE_ID_DO_SOMETHING, myCustomObject);
ActorScheduler.after(5000) // 5000 milliseconds
            .send(message, MainFragment.class);
```

If the <b>MainFragment</b> unregistered itself from <b>ActorSystem</b> before the message is sent, nothing will happen

# Setup android components as actors manually

Remember that you do not need to setup Activities Manually in all cases, so If you choose to Register and Unregister The remaining Android components manually, here is what to be done in every type :

# Setup Fragments (non-support Fragments) Manually

```java
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
```

# Setup Service Manually

```java
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
```

# Listen to ActorSystem.unregister(Actor) through implementing OnActorUnregistered

your Actor can implement <b>OnActorUnregistered</b> to get notified when it is un-registered from the Actor-System, this is Ideal for Actors that are registered and un-registered from out-side there classes, in the next section, you will find heavy use of this interface

# Dependency Injection with @Spawn - Working with Actor-Model Architecture

Starting from version 1.0.0, you can <b>Spawn</b> Actors through annotations, in other words, you can tell the Actor-System to create another Actor for your current Actor, and when your current Actor is un-registered from the system, the spawned Actors are un-registered as well ... notice that Actors are meant to be singletons in there scope, so if you request to Spawn an Actor multiple times in the same scope, only one Actor will be available in this scope.

# Sample Module using ActorLite

This is an example for a full MVC example from Activity to Model to repository to data sources :

Our Activity will create it's Model (which extends the new architecture components ViewModel), and it will register it to the Actor-System, as follows :

```Java
public class MainActivity extends AppCompatActivity implements Actor {

    private Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(this).get(Model.class);
        ActorSystem.register(model);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Message message = new Message(Model.MSG_PING, "message from MainActivity");
        ActorSystem.send(message, Model.class);
    }


    @Override
    public void onMessageReceived(Message message) {
        // handle messages from others
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Override
    protected void onDestroy() {
        ActorSystem.unregister(model);
        if(isFinishing()){
            ActorScheduler.cancel(model.getClass());
        }
        super.onDestroy();
    }
}
```

And Our Model will request from the ActorSystem to spawn a Repository Actor for it, in other words, it requests from ActorSystem to create a Repository instance (if not created), so as soon as this Model is registered to ActorSystem, the Repository Actor will be registered as well :

```java
@Spawn(Repository.class)
public class Model extends ViewModel implements Actor, OnActorUnregistered {

    public static final int MSG_PING = 1;


    @Override
    public void onMessageReceived(Message message) {
        if(message.getId() == MSG_PING) {
            Message newMessage = new Message(Repository.MSG_PING,message.getContent());
            ActorSystem.send(newMessage,Repository.class);
        }
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Schedulers.computation();
    }

    @Override
    public void onUnregister() {
        // clear the state of this Actor in this method
    }
}
```

Our Model requested from the Actor-System to Spawn Repository.java, so the System will create this Actor as long as the Model is registered :

```java
@Spawn({ServerDataSource.class, DatabaseDataSource.class})
public class Repository implements Actor {

    public static final int MSG_PING = 1;

    public Repository(){
        // spawned Actors should have a default constructor
        // or no constructors at all
    }

    @Override
    public void onMessageReceived(Message message) {
        ActorSystem.send(new Message(ServerDataSource.MSG_PING,"message from repository"), ServerDataSource.class);

        ActorSystem.send(new Message(DatabaseDataSource.MSG_PING,"message from repository"), DatabaseDataSource.class);
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Schedulers.computation();
    }
}
```

And Our Repository requested from the Actor-System to Spawn two Actors for it, which are ServerDataSource.java and DatabaseDataSource.java ... so they will be created as long as the Repository is Registered, and they are :

```java
public class ServerDataSource implements Actor, OnActorUnregistered {

    public static final int MSG_PING = 1;
    private final ServerApi serverApi = ...;

    @Override
    public void onMessageReceived(Message message) {
        // handle messages and retrieve data from server
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Schedulers.io();
    }

    @Override
    public void onUnregister() {
        serverApi.close();
    }
}
```

```java
public class DatabaseDataSource implements Actor, OnActorUnregistered {

    public static final int MSG_PING = 1;
    private final Database database = ...;

    @Override
    public void onMessageReceived(Message message) {
        // handle messages and retrieve data from database
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Schedulers.io();
    }

    @Override
    public void onUnregister() {
        database.close();
    }
}
```

 Notice that if you Spawn an actor multiple times in the same scope, only one instance will be created and running, and it will be unregistered when the first Actor requesting it's Spawn is unregistered as well

 You can Spawn all the desired Actors when you start your Activity as follows :

 ```Java
 @Spawn({Model.class, Repository.class, ServerDataSource.class, DatabaseDataSource.class})
 public class MainActivity extends AppCompatActivity implements Actor {
    ...
 }
 ```

So all those Actors will be available as long as the Activity is registered ... in real life, you will not need to Spawn the Model, you will need to spawn Actors starting from the Repository class and it's dependencies


# Tips

To Avoid the big if/else blocks in onMessageReceived(), you can use <b>CommandsMap</b> instead ( https://github.com/Ahmed-Adel-Ismail/CommandsMap ), it is also used in the sample application in this repository, sample code for using <b>CommandsMap</b> is as follows :

```java
@CommandsMapFactory
public class MainActivity extends AppCompatActivity implements Actor {

    public static final int MSG_ONE_ID = 1;
    public static final int MSG_TWO_ID = 1;

    private CommandsMap map = CommandsMap.of(this);

    ...

    @Override
    public void onMessageReceived(Message message) {
        map.execute(message.getId(), message.getContent());
    }

    @Command(MSG_ONE_ID)
    void onMessageOneReceived(String text) {
        // handle Message with ID as 1 and it's Message.getContent()
        // returns String
    }

    @Command(MSG_TWO_ID)
    void onMessageTwoReceived(Integer value) {
        // handle Message with ID as 2 and it's Message.getContent()
        // returns Integer
    }
}
```

# Gradle Dependencies

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency

```gradle
dependencies {
        compile 'com.github.Ahmed-Adel-Ismail:ActorLite:1.0.0'
}
```

# Pro-Guard

```proguard
# Keep default constructors inside classes
-keepclassmembers class * {
   public protected <init>(...);
   <init>(...);
}
```

* Any feedback regarding Proguard, please open an issue with it