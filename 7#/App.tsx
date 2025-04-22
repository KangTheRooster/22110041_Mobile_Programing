import { StatusBar } from "expo-status-bar";
import { useState } from "react";
import {
  StyleSheet,
  Text,
  View,
  TextInput,
  TouchableOpacity,
  FlatList,
  Alert,
  Platform,
} from "react-native";
import { AntDesign } from "@expo/vector-icons";
import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import DateTimePicker from "@react-native-community/datetimepicker";
import { Menu, MenuItem } from "react-native-material-menu";

interface ITodo {
  id: number;
  title: string;
  dueDate: Date | null;
}

function HomeScreen() {
  const [todoList, setTodoList] = useState<ITodo[]>([]);
  const [inputText, setInputText] = useState<string>("");
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [isDatePickerVisible, setDatePickerVisibility] = useState(false);

  const [isMenuVisible, setMenuVisible] = useState(false);

  function random(min: number, max: number) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }

  const addTodo = () => {
    if (inputText.trim() === "") {
      Alert.alert("Error", "Please enter a task name");
      return;
    }
    if (!selectedDate) {
      Alert.alert("Error", "Please select a due date");
      return;
    }
    const todo = {
      id: random(1, 100000000),
      title: inputText,
      dueDate: selectedDate,
    };
    setTodoList([...todoList, todo]);
    setInputText("");
    setSelectedDate(null);
  };

  const deleteTodo = (id: number) => {
    setTodoList(todoList.filter((todo) => todo.id !== id));
  };

  const sortByName = () => {
    setTodoList([...todoList].sort((a, b) => a.title.localeCompare(b.title)));
    setMenuVisible(false);
  };

  const sortByDate = () => {
    setTodoList(
      [...todoList].sort(
        (a, b) => (a.dueDate?.getTime() || 0) - (b.dueDate?.getTime() || 0)
      )
    );
    setMenuVisible(false);
  };

  const handleDateChange = (event: any, date?: Date) => {
    setDatePickerVisibility(Platform.OS === "ios"); // Keep picker visible on iOS until confirm/cancel
    if (date) {
      setSelectedDate(date);
    }
  };

  const renderTodo = ({ item }: { item: ITodo }) => (
    <View style={styles.todoItem}>
      <View>
        <Text style={styles.todoText}>{item.title}</Text>
        <Text style={styles.todoDate}>
          Due: {item.dueDate ? item.dueDate.toLocaleDateString() : "No date"}
        </Text>
      </View>
      <TouchableOpacity onPress={() => deleteTodo(item.id)}>
        <AntDesign name="delete" size={24} color="red" />
      </TouchableOpacity>
    </View>
  );

  return (
    <View style={styles.container}>
      <Text style={styles.header}>ToDo App</Text>

      <View style={styles.inputContainer}>
        <TextInput
          style={styles.input}
          placeholder="Add a new todo..."
          value={inputText}
          onChangeText={setInputText}
          onSubmitEditing={addTodo}
        />
        <TouchableOpacity style={styles.addButton} onPress={addTodo}>
          <AntDesign name="plus" size={24} color="white" />
        </TouchableOpacity>
      </View>

      <TouchableOpacity
        style={styles.dateButton}
        onPress={() => setDatePickerVisibility(true)}
      >
        <Text style={styles.dateButtonText}>
          {selectedDate ? selectedDate.toLocaleDateString() : "Select Due Date"}
        </Text>
      </TouchableOpacity>

      {isDatePickerVisible && (
        <View>
          <DateTimePicker
            value={selectedDate || new Date()}
            mode="date"
            minimumDate={new Date()}
            onChange={handleDateChange}
          />
          {Platform.OS === "ios" && (
            <View style={styles.buttonContainer}>
              <TouchableOpacity
                style={[styles.button, styles.cancelButton]}
                onPress={() => setDatePickerVisibility(false)}
              >
                <Text style={styles.buttonText}>Cancel</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.button, styles.confirmButton]}
                onPress={() => setDatePickerVisibility(false)}
              >
                <Text style={styles.buttonText}>Confirm</Text>
              </TouchableOpacity>
            </View>
          )}
        </View>
      )}

      <Menu
        visible={isMenuVisible}
        anchor={
          <TouchableOpacity
            style={styles.sortButton}
            onPress={() => setMenuVisible(true)}
          >
            <Text style={styles.sortButtonText}>Sort</Text>
          </TouchableOpacity>
        }
        onRequestClose={() => setMenuVisible(false)}
      >
        <MenuItem onPress={sortByName}>Sort by Name</MenuItem>
        <MenuItem onPress={sortByDate}>Sort by Date</MenuItem>
      </Menu>

      <FlatList
        data={todoList}
        renderItem={renderTodo}
        keyExtractor={(item) => item.id.toString()}
        style={styles.list}
      />

      <StatusBar style="auto" />
    </View>
  );
}

export default function App() {
  const Stack = createNativeStackNavigator();

  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen
          name="Home"
          component={HomeScreen}
          options={{ title: "ToDo App" }}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    paddingTop: 40,
    paddingHorizontal: 20,
  },
  header: {
    fontSize: 24,
    fontWeight: "bold",
    marginBottom: 20,
    textAlign: "center",
  },
  inputContainer: {
    flexDirection: "row",
    marginBottom: 10,
  },
  input: {
    flex: 1,
    borderWidth: 1,
    borderColor: "#ddd",
    padding: 10,
    borderRadius: 5,
    marginRight: 10,
    fontSize: 16,
  },
  addButton: {
    backgroundColor: "#007AFF",
    padding: 10,
    borderRadius: 5,
    justifyContent: "center",
    alignItems: "center",
  },
  dateButton: {
    backgroundColor: "#f0f0f0",
    padding: 10,
    borderRadius: 5,
    marginBottom: 20,
    alignItems: "center",
  },
  dateButtonText: {
    fontSize: 16,
    color: "#333",
  },
  sortButton: {
    backgroundColor: "#666",
    padding: 10,
    borderRadius: 5,
    marginBottom: 20,
    alignItems: "center",
  },
  sortButtonText: {
    fontSize: 16,
    color: "#fff",
  },
  todoItem: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    padding: 15,
    backgroundColor: "#f8f8f8",
    borderRadius: 5,
    marginBottom: 10,
  },
  todoText: {
    fontSize: 16,
    flex: 1,
  },
  todoDate: {
    fontSize: 14,
    color: "#666",
  },
  list: {
    flex: 1,
  },
  buttonContainer: {
    flexDirection: "row",
    justifyContent: "space-around",
    marginTop: 10,
  },
  button: {
    padding: 10,
    borderRadius: 5,
    minWidth: 100,
    alignItems: "center",
  },
  cancelButton: {
    backgroundColor: "#ff4444",
  },
  confirmButton: {
    backgroundColor: "#007AFF",
  },
  buttonText: {
    color: "#fff",
    fontSize: 16,
  },
});
