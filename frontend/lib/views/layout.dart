import 'package:ale_okaz/consts/colors.dart';
import 'package:ale_okaz/widgets/bottom_bar.dart';
import 'package:ale_okaz/widgets/top_bar/top_bar.dart';
import 'package:flutter/material.dart';

class Layout extends StatelessWidget {
  final Widget body;
  final bool hasBackButton;
  final bool hasBottomBar;
  const Layout(
      {super.key,
      required this.body,
      this.hasBackButton = false,
      this.hasBottomBar = true});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: primaryBackgroundColor,
      appBar: TopBar(hasBackButton: hasBackButton),
      body: body,
      bottomNavigationBar: hasBottomBar ? const BottomBar() : null,
    );
  }
}
